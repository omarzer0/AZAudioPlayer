package az.zero.azaudioplayer.ui.screens.sort.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.utils.AlbumSortBy
import az.zero.base.utils.toAlbumSortBy
import az.zero.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SortAlbumBottomSheetViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val repository: AudioRepository,
) : ViewModel() {

    val sortAlbumBy = repository.sortAlbumBy.asLiveData()
    var sortAlbumBySingleValue = AlbumSortBy.ASCENDING
        private set

    init {
        viewModelScope.launch {
            val readData =
                dataStoreManager.read(DataStoreManager.SORT_ALBUM_BY,
                    AlbumSortBy.ASCENDING.name)

            sortAlbumBySingleValue = readData.toAlbumSortBy()
        }
    }

    fun onAlbumSortOrderChange(newAudioSortOrder: AlbumSortBy) {
        dataStoreManager.saveSortAlbumBy(newAudioSortOrder.name)
    }
}

