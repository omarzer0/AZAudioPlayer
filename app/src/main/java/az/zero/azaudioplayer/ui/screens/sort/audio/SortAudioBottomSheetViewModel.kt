package az.zero.azaudioplayer.ui.screens.sort.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.utils.AudioSortBy
import az.zero.base.utils.toAudioSortBy
import az.zero.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SortAudioBottomSheetViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val repository: AudioRepository,
) : ViewModel() {

    val sortBy = repository.sortAudioBy.asLiveData()
    var sortBySingleValue = AudioSortBy.DATE_OF_UPDATE
        private set

    init {
        viewModelScope.launch {
            val readData =
                dataStoreManager.read(DataStoreManager.SORT_AUDIO_BY, AudioSortBy.DATE_OF_UPDATE.name)

            sortBySingleValue = readData.toAudioSortBy()
        }
    }

    fun onAudioSortOrderChange(newAudioSortOrder: AudioSortBy) {
        dataStoreManager.saveSortAudioBy(newAudioSortOrder.name)
    }
}

