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
import az.zero.azaudioplayer.media.player.extensions.id
import az.zero.azaudioplayer.media.player.extensions.isPlayEnabled
import az.zero.azaudioplayer.media.player.extensions.isPlaying
import az.zero.azaudioplayer.media.player.extensions.isPrepared
import az.zero.azaudioplayer.media.service.AudioService
import javax.inject.Inject

class AudioServiceConnection @Inject constructor(
    val context: Context
) {
    val rootMediaId: String get() = mediaBrowser.root

    val dataChanged: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _audioConnectionData = MutableLiveData<AudioConnectionData>()
    val audioConnectionData: LiveData<AudioConnectionData> = _audioConnectionData

//    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
//    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

//    private val _curPlayingSong = MutableLiveData<MediaMetadataCompat?>()
//    val curPlayingSong: LiveData<MediaMetadataCompat?> = _curPlayingSong

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)

    private val mediaBrowser = MediaBrowserCompat(
        context,
        ComponentName(context, AudioService::class.java),
        mediaBrowserConnectionCallback, null
    ).apply { connect() }

    private lateinit var mediaController: MediaControllerCompat

    private val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls

    fun playPauseOrToggle(mediaItem: String, toggle: Boolean = false) {
        val playState = audioConnectionData.value?.playbackState
        val isPrepared = audioConnectionData.value?.playbackState?.isPrepared ?: false
        val currentSongId = audioConnectionData.value?.nowPlaying?.description?.mediaId

        // TODO fix not pausing
        Log.e("playPauseOrToggle", "playPauseOrToggle:$isPrepared $currentSongId $mediaItem")
        if (isPrepared && mediaItem == currentSongId) {
            // If we call this fun with the same current playing song
            // We can pause it if playbackState.isPlaying
            // We can play it again from start if playbackState.isPlayEnabled
            playState?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if (toggle) transportControls.pause()
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> Unit
                }
            }
        } else {
            // New song so play it
            transportControls.playFromMediaId(mediaItem, null)
        }
    }


    inner class MediaBrowserConnectionCallback(context: Context) :
        MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController =
                MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                    registerCallback(MediaControllerCallback())
                }
            updateState(audioConnectionData.value?.copy(isConnected = true))

        }

        override fun onConnectionSuspended() {
            updateState(audioConnectionData.value?.copy(isConnected = false))
        }

        override fun onConnectionFailed() {
            updateState(audioConnectionData.value?.copy(isConnected = false))
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            // Player state ex: (play/pause) changed
            Log.e("playPauseOrToggle", "playPauseOrToggle:dasdsa${state}")
            updateState(
                audioConnectionData.value?.copy(
                    playbackState = state ?: EMPTY_PLAYBACK_STATE
                )
            )
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            // Current playing song changed
            updateState(
                audioConnectionData.value?.copy(
                    nowPlaying = if (metadata?.id == null) NOTHING_PLAYING else metadata
                )
            )
        }

        // onSessionEvent to handle errors
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            Log.e("MainViewModel", "onQueueChanged: $queue")
            dataChanged.postValue(true)
        }
    }

    private fun updateState(audioConnectionData: AudioConnectionData?) {
        audioConnectionData?.let {
            _audioConnectionData.postValue(it)
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

data class AudioConnectionData(
    val isConnected: Boolean = false,
    val networkFailure: Boolean = false,
    val playbackState: PlaybackStateCompat? = EMPTY_PLAYBACK_STATE,
    val nowPlaying: MediaMetadataCompat? = NOTHING_PLAYING,
)