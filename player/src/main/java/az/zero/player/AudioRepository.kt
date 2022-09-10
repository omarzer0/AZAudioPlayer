package az.zero.player

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
import androidx.lifecycle.distinctUntilChanged
import az.zero.azaudioplayer.media.player.extensions.id
import az.zero.azaudioplayer.media.player.extensions.isPlayEnabled
import az.zero.azaudioplayer.media.player.extensions.isPlaying
import az.zero.azaudioplayer.media.player.extensions.isPrepared
import az.zero.base.di.ApplicationScope
import az.zero.base.utils.AudioActions
import az.zero.datastore.DataStoreManager
import az.zero.datastore.DataStoreManager.Companion.LAST_PLAYED_AUDIO_ID_KEY
import az.zero.datastore.DataStoreManager.Companion.REPEAT_MODE
import az.zero.db.AudioDao
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import az.zero.player.service.AudioService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class AudioRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioDao: AudioDao,
    @ApplicationScope private val scope: CoroutineScope,
    private val dataStoreManager: DataStoreManager,
) {

    fun getAllAudio() = audioDao.getAllDbAudio()

    fun getAlbumWithAudio() = audioDao.getAlbumWithAudio()

    fun getArtistWithAudio() = audioDao.getArtistWithAudio()

    fun getAllPlayLists() = audioDao.getAllPlayLists()

    private val _nowPlayingAudio = MutableLiveData(EMPTY_AUDIO)
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

    fun playPauseOrToggle(mediaItem: String) {
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
            dataStoreManager.saveLastPlayedAudio(mediaItem)
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

    fun audioAction(action: AudioActions) {
        when (action) {
            AudioActions.Pause -> transportControls.pause()
            AudioActions.Play -> transportControls.play()
            is AudioActions.Toggle -> playPauseOrToggle(action.audioDataId)
        }
    }

    suspend fun addOrRemoveFromFavouritePlayList(dbAudio: DBAudio) {
        audioDao.addOrRemoveFromFavouritePlayList(dbAudio)
    }

    suspend fun addPlayList(dbPlaylist: DBPlaylist) {
        audioDao.addPlayList(dbPlaylist)
    }

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
                val audio = metadata?.id?.let { audioDao.getAudioById(it) } ?: EMPTY_AUDIO
                _nowPlayingAudio.postValue(audio)
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

//    init {
//        scope.launch {
//            val lastAudioId = dataStoreManager.read(LAST_PLAYED_AUDIO_ID_KEY, "")
//            val audio = if (lastAudioId.isEmpty()) EMPTY_AUDIO
//            else dao.getAudioById(lastAudioId) ?: EMPTY_AUDIO
//            _nowPlayingAudio.postValue(audio)
//        }
//    }
}

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = Builder()
    .setState(STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()

val EMPTY_AUDIO = DBAudio("", "", "", 0, "", "", "", "", 0)
