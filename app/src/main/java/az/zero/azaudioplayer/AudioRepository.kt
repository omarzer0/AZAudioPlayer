package az.zero.azaudioplayer

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDeepLinkBuilder
import az.zero.azaudioplayer.ui.MainActivity
import az.zero.azaudioplayer.utils.tryWithHandledCatch
import az.zero.base.di.ApplicationScope
import az.zero.base.utils.AudioActions
import az.zero.base.utils.PlayingListFrom
import az.zero.base.utils.toAlbumSortBy
import az.zero.base.utils.toAudioSortBy
import az.zero.datastore.DataStoreManager
import az.zero.datastore.DataStoreManager.Companion.LAST_PLAYED_AUDIO_ID_KEY
import az.zero.datastore.DataStoreManager.Companion.REPEAT_MODE
import az.zero.db.AudioDao
import az.zero.db.entities.DBAlbumWithAudioList
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import az.zero.player.audio_data_source.AudioDataSource
import az.zero.player.extensions.*
import az.zero.player.service.AudioService
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioDao: AudioDao,
    @ApplicationScope private val scope: CoroutineScope,
    private val dataStoreManager: DataStoreManager,
    private val audioDataSource: AudioDataSource,
) {

    val sortAudioBy = dataStoreManager.sortAudioByFlow.map {
        it.toAudioSortBy()
    }

    val sortAlbumBy = dataStoreManager.sortAlbumByFlow.map {
        it.toAlbumSortBy()
    }

    @OptIn(ExperimentalPagerApi::class)
    fun pendingIntent(): PendingIntent {
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav_graph)
            .setComponentName(MainActivity::class.java)
            .setDestination(R.id.playerBottomSheetFragment)
            .createPendingIntent()
    }

    val allAudio: Flow<List<DBAudio>> = sortAudioBy.flatMapLatest {
        audioDao.getAllDbAudio(it)
    }

    val allAlbumsWithAudio: Flow<List<DBAlbumWithAudioList>> =
        sortAlbumBy.flatMapLatest { albumSortOrder ->
            audioDao.getAlbumWithAudio(albumSortOrder)
        }

    fun getArtistWithAudio() = audioDao.getArtistWithAudio()

    fun getAllPlayLists() = audioDao.getAllPlayLists()

    fun getAllPlayListsWithoutFavouritePlaylist() = audioDao.getAllPlayListsWithoutFavouritePlaylist()

    private var _nowPlayingAudio = MutableLiveData(EMPTY_AUDIO)
    val nowPlayingDBAudio: LiveData<DBAudio?> = _nowPlayingAudio

    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    private val _playbackState = MutableLiveData(EMPTY_PLAYBACK_STATE)
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

    private val _repeatMode = MutableLiveData(REPEAT_MODE_ALL)
    val repeatMode: LiveData<Int> = _repeatMode

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback()

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context, AudioService::class.java),
        mediaBrowserConnectionCallback, null
    ).apply { connect() }

    private lateinit var mediaController: MediaControllerCompat

    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    private fun playPauseOrToggle(mediaItem: String, newAudioList: List<DBAudio>?) {
        if (mediaItem.isEmpty()) return
        val playState = _playbackState.value
        val isPrepared = _playbackState.value?.isPrepared ?: false
        val currentSongId = _nowPlayingAudio.value?.data

        if (isPrepared && mediaItem == currentSongId) {
            // If we call this fun with the same current playing song
            // We can pause it if playbackState.isPlaying
            // We can play it again from start if playbackState.isPlayEnabled
            playState?.let { playbackState ->
                when {
                    playbackState.isPlaying -> transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> Unit
                }
            }
        } else {
            // New song so play it
            playNewAudio(newAudioList, mediaItem)
        }
    }

    private fun playNewAudio(newAudioList: List<DBAudio>?, mediaItem: String) {
        if (newAudioList != null) {
            newAudioChosen(newAudioList, mediaItem)
        } else {
            // if the passed list is null play from all audio list
            scope.launch {
                val allAudio = audioDao.getAllDbAudioSingleList()
                newAudioChosen(allAudio, mediaItem)
            }
        }
    }

    private fun newAudioChosen(newAudioList: List<DBAudio>, mediaItem: String) {
        tryWithHandledCatch {
            audioDataSource.updateAudioList(newAudioList)
            transportControls.playFromMediaId(mediaItem, null)
        }
    }

    fun playOrPause() {
        val isPlaying = _playbackState.value?.state == STATE_PLAYING
        if (isPlaying) transportControls.pause()
        else transportControls.play()
    }

    fun seekTo(position: Long) {
        transportControls.seekTo(position)
    }

    fun skipToNext() {
        transportControls.skipToNext()
    }

    fun skipToPrevious() {
        transportControls.skipToPrevious()
    }

    fun changeRepeatMode() {
        scope.launch {
            val oldRepeatMode: Int = dataStoreManager.read(REPEAT_MODE, REPEAT_MODE_ALL)
            val repeatMode = if (oldRepeatMode == REPEAT_MODE_ALL) REPEAT_MODE_ONE
            else REPEAT_MODE_ALL
            transportControls.setRepeatMode(repeatMode)
            _repeatMode.postValue(repeatMode)
            dataStoreManager.saveRepeatMode(repeatMode)
        }
    }

    fun getPlayBackState(): PlaybackStateCompat = _playbackState.value ?: EMPTY_PLAYBACK_STATE

    /**
     * @param action defines what AudioActions should the player do (play, pause, or toggle)
     * @param newAudioList if null is passed all audio files will be used and if not null
     * the player will play that list
     * */
    fun audioAction(
        action: AudioActions,
        newAudioList: List<DBAudio>?,
        playingListFrom: PlayingListFrom,
    ) {
        when (action) {
            AudioActions.Pause -> transportControls.pause()
            AudioActions.Play -> transportControls.play()
            AudioActions.PlayAll -> {
                // Don't complete if the list is null or empty
                if (newAudioList.isNullOrEmpty()) return
                playPauseOrToggle(newAudioList[0].data, newAudioList)
            }
            is AudioActions.Toggle -> playPauseOrToggle(action.audioDataId, newAudioList)
        }

    }

    suspend fun addOrRemoveFromFavouritePlayList(dbAudio: DBAudio) {
        audioDao.addOrRemoveFromFavouritePlayList(dbAudio)
//        val audio = audioDao.getAudioById(dbAudio.data) ?: EMPTY_AUDIO
//        _nowPlayingAudio.postValue(audio)
        getUpdatedCurrentlyPlaying(dbAudio.data)
    }

    suspend fun addPlayList(dbPlaylist: DBPlaylist) = audioDao.addPlayList(dbPlaylist)

    suspend fun deletePlayListById(playlistId: String) = audioDao.deletePlaylistById(playlistId)

    suspend fun getSinglePlayListById(playlistId: String) =
        audioDao.getSinglePlaylistById(playlistId)

    suspend fun getFavouritePlaylist() = audioDao.getFavouritePlaylist()

    suspend fun clearFavList() = audioDao.clearFavList(context)

    fun getPlaylistById(playlistId: String) = audioDao.getPlaylistById(playlistId)


    suspend fun getAllDbAudioSingleListByQuery(query: String): List<DBAudio>? {
        return audioDao.getAllDbAudioSingleListByQuery(query.trim())
    }

    inner class MediaBrowserConnectionCallback : MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController =
                MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                    registerCallback(MediaControllerCallback())
                }
            _isConnected.postValue(true)
            scope.launch {
                // Prepare exo player with saved repeat mode
                val oldRepeatMode: Int = dataStoreManager.read(REPEAT_MODE, REPEAT_MODE_ALL)
                _repeatMode.postValue(oldRepeatMode)
                transportControls.setRepeatMode(oldRepeatMode)

                // Prepare exo player with the last played song
                val lastAudioId = dataStoreManager.read(LAST_PLAYED_AUDIO_ID_KEY, "")
                if (lastAudioId.isNotEmpty())
                    transportControls.prepareFromMediaId(lastAudioId, Bundle.EMPTY)
            }

        }

        override fun onConnectionSuspended() {
            _isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            _isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            // Player state ex: (play/pause) changed
            _playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            // Current playing song changed
            scope.launch {
//                val audio = metadata?.id?.let { audioDao.getAudioById(it) } ?: EMPTY_AUDIO
//                _nowPlayingAudio.postValue(audio)
                val id = metadata?.id ?: return@launch
                getUpdatedCurrentlyPlaying(id)
            }
        }

        // onSessionEvent to handle errors
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            SHUFFLE_MODE_GROUP
            SHUFFLE_MODE_INVALID
            SHUFFLE_MODE_ALL
        }
    }

    suspend fun getUpdatedCurrentlyPlaying(audioId: String) {
        val audio = audioDao.getAudioById(audioId) ?: EMPTY_AUDIO
        val existingAudio = _nowPlayingAudio.value ?: return
        if (audio != existingAudio) {
            _nowPlayingAudio.postValue(audio)
            dataStoreManager.saveLastPlayedAudio(audio.data)
        }
    }

    init {
        audioDataSource.setDestinationAndGraphIds(pendingIntent())

        scope.launch {
            val allAudio = audioDao.getAllDbAudioSingleList()
            audioDataSource.updateAudioList(allAudio)
        }
    }
}
