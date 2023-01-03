package az.zero.azaudioplayer.ui.screens.scan_local_files

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.di.ApplicationScope
import az.zero.datastore.DataStoreManager
import az.zero.datastore.DataStoreManager.Companion.SKIP_ANDROID_FILES
import az.zero.datastore.DataStoreManager.Companion.SKIP_RECORDINGS_FILES
import az.zero.db.helpers.AudioDbHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanLocalViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val audioDbHelper: AudioDbHelper,
    private val dataStoreManager: DataStoreManager,
    @ApplicationScope private val appScope: CoroutineScope,
) : ViewModel() {

    private val scanState = MutableStateFlow(ScanLocalState.ScanState.INITIAL)

    val state = combine(
        audioRepository.allAudio,
        scanState,
        dataStoreManager.skipRecordingsDirectoryAudios,
        dataStoreManager.skipAndroidDirectoryAudios
    ) { audiosSize, scanState, skipRecordingsDirectoryAudios, skipAndroidDirectoryAudios ->

        ScanLocalState(
            scanState = scanState,
            audiosFound = audiosSize.size,
            skipRecordingsDirectoryAudios = skipRecordingsDirectoryAudios,
            skipAndroidDirectoryAudios = skipAndroidDirectoryAudios
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ScanLocalState()
    )

    fun onSearchClick() {
        searchForData()
    }

    init {
        searchForData()
    }

    fun onSkipRecordingsDirectoryChange(isChecked: Boolean) {
        appScope.launch {
            dataStoreManager.write(SKIP_RECORDINGS_FILES, isChecked)
            searchForData()
        }
    }

    fun onSkipAndroidDirectoryChange(isChecked: Boolean) {
        appScope.launch {
            dataStoreManager.write(SKIP_ANDROID_FILES, isChecked)
            searchForData()
        }
    }

    private fun searchForData() {
        appScope.launch {
            scanState.emit(ScanLocalState.ScanState.LOADING)
            val skipRecordings = dataStoreManager.read(SKIP_RECORDINGS_FILES, true)
            val skipAndroidFiles = dataStoreManager.read(SKIP_ANDROID_FILES, true)

            delay(500)
            audioDbHelper.searchForNewAudios(
                skipRecordings = skipRecordings,
                skipAndroidFiles = skipAndroidFiles,
                onFinish = {
                    scanState.emit(ScanLocalState.ScanState.DONE)
                })
        }
    }
}

data class ScanLocalState(
    val scanState: ScanState = ScanState.INITIAL,
    val audiosFound: Int = 0,
    val skipRecordingsDirectoryAudios: Boolean = true,
    val skipAndroidDirectoryAudios: Boolean = true,
) {
    enum class ScanState {
        LOADING,
        DONE,
        INITIAL
    }
}

