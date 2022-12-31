package az.zero.azaudioplayer.ui.screens.details.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.utils.AudioActions
import az.zero.base.utils.PlayingListFrom
import az.zero.db.entities.DBAudio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
) : ViewModel() {

    val currentPlayingAudio = audioRepository.nowPlayingDBAudio.distinctUntilChanged()
    val playbackState = audioRepository.playbackState

    fun addOrRemoveFromFavourite(DBAudio: DBAudio) {
        viewModelScope.launch {
            audioRepository.addOrRemoveFromFavouritePlayList(DBAudio)
        }
    }

    fun playOrPause() {
        audioRepository.playOrPause()
    }

    fun audioAction(action: AudioActions, newAudioList: List<DBAudio>?) {
        audioRepository.audioAction(
            action = action,
            newAudioList = newAudioList,
            playingListFrom = PlayingListFrom.ARTIST
        )
    }
}

