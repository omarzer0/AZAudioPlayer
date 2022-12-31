package az.zero.azaudioplayer.ui.screens.manage.playlist_manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.azaudioplayer.data.local.model.playlist.SelectablePlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistManageViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
) : ViewModel() {

    private val _allList = audioRepository.getAllPlayListsWithoutFavouritePlaylist().asFlow()
    private val _selectedIds = MutableStateFlow(mutableSetOf<String>())
    private val _isDeleteDialogShown = MutableStateFlow(false)

    val state = combine(
        _allList,
        _selectedIds,
        _isDeleteDialogShown
    ) { newList, newIds, isDeleteDialogShown ->
        val list = newList.map { dbList ->
            SelectablePlaylist(playlist = dbList, isSelected = newIds.contains(dbList.name))
        }
        val allListSelected = list.isNotEmpty() && (list.size == newIds.size)
        val areActionsEnabled = list.isNotEmpty() && newIds.size > 0

        PlaylistManageState(
            selectablePlaylists = list,
            allListSelected = allListSelected,
            areActionsEnabled = areActionsEnabled,
            deleteDialogShown = isDeleteDialogShown
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PlaylistManageState()
    ).asLiveData()


    fun onPlaylistSelect(playlistId: String, isSelected: Boolean) {
        viewModelScope.launch {
            val newSet = _selectedIds.value.toMutableSet()
            if (isSelected) newSet.remove(playlistId)
            else newSet.add(playlistId)
            _selectedIds.emit(newSet)
        }
    }

    fun onSelectAllButtonClick(isActive: Boolean) {
        viewModelScope.launch {
            if (isActive) _selectedIds.emit(mutableSetOf())
            else {
                val newList =
                    state.value?.selectablePlaylists?.map { it.playlist.name } ?: return@launch
                _selectedIds.emit(newList.toMutableSet())
            }
        }
    }

    fun onDeleteAllSelectedClick() {
        viewModelScope.launch {
            _isDeleteDialogShown.emit(true)
        }
    }

    fun dismissDialog() {
        viewModelScope.launch {
            _isDeleteDialogShown.emit(false)
        }
    }

    fun onDeleteConfirmed() {
        viewModelScope.launch {
            _isDeleteDialogShown.emit(false)

            _selectedIds.value.clear()
            _selectedIds.emit(_selectedIds.value)

            state.value?.selectablePlaylists?.filter {
                it.isSelected
            }?.forEach {
                launch { audioRepository.deletePlayListById(it.playlist.name) }
            }
        }
    }

}

data class PlaylistManageState(
    val selectablePlaylists: List<SelectablePlaylist> = emptyList(),
    val areActionsEnabled: Boolean = false,
    val allListSelected: Boolean = false,
    val deleteDialogShown: Boolean = false,
)

