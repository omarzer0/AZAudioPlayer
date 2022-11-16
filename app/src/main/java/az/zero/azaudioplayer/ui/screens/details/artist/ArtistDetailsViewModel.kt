package az.zero.azaudioplayer.ui.screens.details.artist

import androidx.lifecycle.ViewModel
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
) : ViewModel() {
    fun audioAction(action: AudioActions, newAudioList: List<DBAudio>?) {
        audioRepository.audioAction(action, newAudioList = newAudioList)
    }
}

