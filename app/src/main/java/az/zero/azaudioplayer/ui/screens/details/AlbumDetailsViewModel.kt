package az.zero.azaudioplayer.ui.screens.details

import androidx.lifecycle.ViewModel
import az.zero.azaudioplayer.domain.use_case.AudioActionUseCase
import az.zero.azaudioplayer.utils.AudioActions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val audioActionUseCase: AudioActionUseCase
) : ViewModel() {
    fun audioAction(action: AudioActions) {
        audioActionUseCase(action)
    }
}

