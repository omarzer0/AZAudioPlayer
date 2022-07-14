package az.zero.azaudioplayer.ui.screens.player

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import az.zero.azaudioplayer.media.player.AudioServiceConnection
import az.zero.azaudioplayer.media.player.currentPlayBackPosition
import az.zero.azaudioplayer.ui.utils.POSITION_UPDATE_INTERVAL_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerBottomSheetViewModel @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection
) : ViewModel() {

    private var updatePosition = true

    fun seekToPosition(position: Long) {
        audioServiceConnection.seekTo(position)
    }

    fun playPrevious() {
        audioServiceConnection.skipToPrevious()
    }

    fun playOrPause() {
        audioServiceConnection.playOrPause()
    }

    fun playNext() {
        audioServiceConnection.skipToNext()
    }

    private val handler = Handler(Looper.getMainLooper())

    private val _currentPosition = MutableLiveData<Long>().apply {
        postValue(audioServiceConnection.getPlayBackState().currentPlayBackPosition)
    }
    val currentPosition: LiveData<Long> = _currentPosition

    private fun checkPlaybackPosition() {
        handler.postDelayed({
            val currPosition = audioServiceConnection.getPlayBackState().currentPlayBackPosition
            if (_currentPosition.value != currPosition)
                _currentPosition.postValue(currPosition)
            if (updatePosition) checkPlaybackPosition()

            Log.e("PlayerScreenIn", "currentPosition: ${currentPosition.value}")

        }, POSITION_UPDATE_INTERVAL_MILLIS)
    }

//    val currentPlayingAudio = audioServiceConnection.audioConnectionData.distinctUntilChanged().also { data ->
//        data.observeForever {
//            Log.e("imageTestVM", "changed ${it.nowPlayingAudio.displayName}")
//            Log.e("imageTestVM", "changed ${it.nowPlayingAudio.cover}")
//        }
//    }

    val currentPlayingAudio = audioServiceConnection.nowPlayingAudio.distinctUntilChanged()
    val playbackState = audioServiceConnection.playbackState.distinctUntilChanged()

    init {
        checkPlaybackPosition()
    }

    override fun onCleared() {
        super.onCleared()
        updatePosition = false
    }

    fun pause() {
        audioServiceConnection.pause()
    }

    fun play() {
        audioServiceConnection.play()
    }
}