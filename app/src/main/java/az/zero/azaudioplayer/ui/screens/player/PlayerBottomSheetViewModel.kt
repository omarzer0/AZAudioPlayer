package az.zero.azaudioplayer.ui.screens.player

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import az.zero.azaudioplayer.media.player.extensions.currentPlayBackPosition
import az.zero.azaudioplayer.ui.utils.POSITION_UPDATE_INTERVAL_MILLIS
import az.zero.db.entities.DBAudio
import az.zero.player.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerBottomSheetViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
) : ViewModel() {

    private var updatePosition = true
    val currentPlayingAudio = audioRepository.nowPlayingDBAudio.distinctUntilChanged()
    val playbackState = audioRepository.playbackState.distinctUntilChanged()
    val repeatMode = audioRepository.repeatMode

    fun seekToPosition(position: Long) {
        audioRepository.seekTo(position)
    }

    fun playPrevious() {
        audioRepository.skipToPrevious()
    }

    fun playOrPause() {
        audioRepository.playOrPause()
    }

    fun playNext() {
        audioRepository.skipToNext()
    }

    fun changeRepeatMode() {
        audioRepository.changeRepeatMode()
    }

    private val handler = Handler(Looper.getMainLooper())

    private val _currentPosition = MutableLiveData<Long>().apply {
        postValue(audioRepository.getPlayBackState().currentPlayBackPosition)
    }
    val currentPosition: LiveData<Long> = _currentPosition

    private fun checkPlaybackPosition() {
        handler.postDelayed({
            val currPosition = audioRepository.getPlayBackState().currentPlayBackPosition
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

    fun addOrRemoveFromFavourite(DBAudio: DBAudio) {
        viewModelScope.launch {
            audioRepository.addOrRemoveFromFavouritePlayList(DBAudio)
        }
    }
}