package az.zero.azaudioplayer.ui.screens.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import az.zero.azaudioplayer.data.db.AudioDao
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.media.player.AudioServiceConnection
import az.zero.azaudioplayer.media.player.currentPlayBackPosition
import az.zero.azaudioplayer.ui.utils.POSITION_UPDATE_INTERVAL_MILLIS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerBottomSheetViewModel @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection,
    private val dao: AudioDao
) : ViewModel() {

    private var updatePosition = true
    val currentPlayingAudio = audioServiceConnection.nowPlayingAudio.distinctUntilChanged()
    val playbackState = audioServiceConnection.playbackState.distinctUntilChanged()
    val repeatMode = audioServiceConnection.repeatMode

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

    fun changeRepeatMode() {
        audioServiceConnection.changeRepeatMode()
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
        }, POSITION_UPDATE_INTERVAL_MILLIS)
    }


    init {
        checkPlaybackPosition()
    }

    override fun onCleared() {
        super.onCleared()
        updatePosition = false
    }

    fun addOrRemoveFromFavourite(audio: Audio) {
        viewModelScope.launch {
            dao.addOrRemoveFromFavouritePlayList(audio)
        }
    }
}