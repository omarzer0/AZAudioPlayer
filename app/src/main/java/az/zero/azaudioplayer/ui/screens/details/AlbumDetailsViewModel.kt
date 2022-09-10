package az.zero.azaudioplayer.ui.screens.details

import androidx.lifecycle.ViewModel
import az.zero.base.utils.AudioActions
import az.zero.player.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {
    fun audioAction(action: AudioActions) {
        audioRepository.audioAction(action)
    }
}

