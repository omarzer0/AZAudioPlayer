package az.zero.azaudioplayer.media.player

import android.content.ComponentName
import android.content.Context
import android.os.SystemClock
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
    private val _nowPlayingAudio = MutableLiveData(EMPTY_AUDIO)
    val nowPlayingAudio: LiveData<Audio?> = _nowPlayingAudio

    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected

    private val _playbackState = MutableLiveData(EMPTY_PLAYBACK_STATE)
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

//    private val _audioConnectionData = MutableLiveData<AudioConnectionData>()
//    val audioConnectionData: LiveData<AudioConnectionData> = _audioConnectionData

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
            // TODO save audio id to dataStore
            transportControls.playFromMediaId(mediaItem, null)
        }
    }

    fun playOrPause() {
        val isPlaying = _playbackState.value?.state == PlaybackStateCompat.STATE_PLAYING
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

    fun pause() {
        transportControls.pause()
    }

    fun play() {
        transportControls.play()
    }

    inner class MediaBrowserConnectionCallback :
        MediaBrowserCompat.ConnectionCallback() {

        override fun onConnected() {
            mediaController =
                MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                    registerCallback(MediaControllerCallback())
                }
            _isConnected.postValue(true)

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
            Log.e("playPauseOrToggle", "playPauseOrToggle:dasdsa${state?.position}")
            _playbackState.postValue(state ?: EMPTY_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            // Current playing song changed
            scope.launch {
                val audio = metadata?.id?.let { dao.getAudioById(it) } ?: EMPTY_AUDIO
                _nowPlayingAudio.postValue(audio)
            }
        }

        // onSessionEvent to handle errors
        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)

        }
    }

    fun getPlayBackState(): PlaybackStateCompat = _playbackState.value ?: EMPTY_PLAYBACK_STATE


    init {
        scope.launch {
            // TODO replace with last played song
            _nowPlayingAudio.postValue(EMPTY_AUDIO)
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

val EMPTY_AUDIO = Audio("", "", "", 0, "", "", "", "", 0)

inline val PlaybackStateCompat.currentPlayBackPosition: Long
    get() = if (state == PlaybackStateCompat.STATE_PLAYING) {
        val timeDelta = SystemClock.elapsedRealtime() - lastPositionUpdateTime
        (position + (timeDelta * playbackSpeed)).toLong()
    } else {
        position
    }


//data class AudioConnectionData(
//    val isConnected: Boolean = false,
//    val networkFailure: Boolean = false,
//    val playbackState: PlaybackStateCompat? = EMPTY_PLAYBACK_STATE,
//    val nowPlayingAudio: Audio = EMPTY_AUDIO,
//)