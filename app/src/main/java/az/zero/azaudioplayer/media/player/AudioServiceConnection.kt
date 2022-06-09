package az.zero.azaudioplayer.media.player

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import az.zero.azaudioplayer.data.db.AudioDao
import az.zero.azaudioplayer.di.ApplicationScope
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.media.player.extensions.id
import az.zero.azaudioplayer.media.player.extensions.isPlayEnabled
import az.zero.azaudioplayer.media.player.extensions.isPlaying
import az.zero.azaudioplayer.media.player.extensions.isPrepared
import az.zero.azaudioplayer.media.service.AudioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioServiceConnection @Inject constructor(
    val context: Context,
    val dao: AudioDao,
    @ApplicationScope val scope: CoroutineScope
) {
//    val rootMediaId: String get() = mediaBrowser.root

//    val dataChanged: MutableLiveData<Boolean> = MutableLiveData(false)

//    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
//    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

//    private val _curPlayingAudio = MutableLiveData<MediaMetadataCompat?>()
//    val curPlayingAudio: LiveData<MediaMetadataCompat?> = _curPlayingAudio

    private val _audioConnectionData = MutableLiveData<AudioConnectionData>()
    val audioConnectionData: LiveData<AudioConnectionData> = _audioConnectionData

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
        val playState = _audioConnectionData.value?.playbackState
        val isPrepared = _audioConnectionData.value?.playbackState?.isPrepared ?: false
        val currentSongId = _audioConnectionData.value?.nowPlayingAudio?.data

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
            // TODO save audio id to dataStore
            transportControls.playFromMediaId(mediaItem, null)
        }
    }


    inner class MediaBrowserConnectionCallback :
        MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController =
                MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                    registerCallback(MediaControllerCallback())
                }
            updateState(_audioConnectionData.value?.copy(isConnected = true))

        }

        override fun onConnectionSuspended() {
            updateState(_audioConnectionData.value?.copy(isConnected = false))
        }

        override fun onConnectionFailed() {
            updateState(_audioConnectionData.value?.copy(isConnected = false))
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            // Player state ex: (play/pause) changed
            Log.e("playPauseOrToggle", "playPauseOrToggle:dasdsa${state}")
            updateState(
                _audioConnectionData.value?.copy(
                    playbackState = state ?: EMPTY_PLAYBACK_STATE
                )
            )
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            // Current playing song changed

            val data = metadata?.id
            if (data == null || data == _audioConnectionData.value?.nowPlayingAudio?.data) return
            Log.e("currentlyPlaying32", "$data")

            scope.launch {
                val audio = dao.getAudioById(data) ?: return@launch
                updateState(_audioConnectionData.value?.copy(nowPlayingAudio = audio))
            }
        }

        // onSessionEvent to handle errors
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            Log.e("MainViewModel", "onQueueChanged: $queue")
//            dataChanged.postValue(true)
        }
    }

    private fun updateState(audioConnectionData: AudioConnectionData?) {
        audioConnectionData?.let {
            Log.e("currentlyPlaying3", "$it")
            _audioConnectionData.postValue(it)
        }
    }

    init {
        scope.launch {
            // TODO replace with last played song
            val id =
                "/storage/emulated/0/YoWhatsApp/Media/YoWhatsApp Audio/AUD-20211118-WA0000.opus"
            val audio = dao.getAudioById(id)

            updateState(
                AudioConnectionData(
                    nowPlayingAudio = audio ?: EMPTY_AUDIO
                )
            )
        }
    }
}

@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()

val EMPTY_AUDIO = Audio("", "", "", 0, "", "", "", "")

data class AudioConnectionData(
    val isConnected: Boolean = false,
    val networkFailure: Boolean = false,
    val playbackState: PlaybackStateCompat? = EMPTY_PLAYBACK_STATE,
    val nowPlayingAudio: Audio = EMPTY_AUDIO,
)