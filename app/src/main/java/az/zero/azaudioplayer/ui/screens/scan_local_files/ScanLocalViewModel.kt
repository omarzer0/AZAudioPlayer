package az.zero.azaudioplayer.ui.screens.scan_local_files

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.db.helpers.AudioDbHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanLocalViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val audioDbHelper: AudioDbHelper,
) : ViewModel() {

    private val audiosSize = audioRepository.allAudio.map { it.size }
    private val scanState = MutableStateFlow(ScanLocalState.ScanState.INITIAL)

    val state = combine(
        audiosSize, scanState
    ) { audiosSize, scanState ->

        ScanLocalState(
            scanState = scanState,
            audiosFound = audiosSize
        )
    }

    fun onSearchClick() {
        searchForData()
    }

    init {
        searchForData()
    }

    private fun searchForData() {
        viewModelScope.launch {
            scanState.emit(ScanLocalState.ScanState.LOADING)
            delay(1000)
            audioDbHelper.searchForNewAudios(onFinish = {
                scanState.emit(ScanLocalState.ScanState.DONE)
            })
        }
    }
}

data class ScanLocalState(
    val scanState: ScanState = ScanState.INITIAL,
    val audiosFound: Int = 0,
) {
    enum class ScanState {
        LOADING,
        DONE,
        INITIAL
    }
}

