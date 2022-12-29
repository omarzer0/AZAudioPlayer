package az.zero.azaudioplayer.ui.screens.add_audio_to_playlist

import android.util.Log
import androidx.lifecycle.*
import az.zero.azaudioplayer.AudioRepository
import az.zero.azaudioplayer.data.local.model.audio.SelectableAudio
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAudioToPlaylistViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val stateHandler: SavedStateHandle
) : ViewModel() {
    private val playlistName = stateHandler.get<String>("playlistName") ?: ""

    val currentPlayingAudio = audioRepository.nowPlayingDBAudio.distinctUntilChanged()

    private val _allAudio = MutableLiveData<List<SelectableAudio>>()
    val allDBAudio: LiveData<List<SelectableAudio>> = _allAudio

    private var searchJob: Job? = null

    fun searchAudios(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val filteredList = audioRepository.getAllDbAudioSingleListByQuery(query)?.map { audio ->
                SelectableAudio(
                    audio = audio,
                    selected = selectedAudioIds.any { it == audio.data }
                )
            } ?: emptyList()

            _allAudio.postValue(filteredList)
        }
    }

    private val selectedAudioIds = mutableSetOf<String>()

    fun addOrRemoveAudio(audioId: String) {
        val index = selectedAudioIds.indexOf(audioId)
        if (index == -1) selectedAudioIds.add(audioId)
        else selectedAudioIds.remove(audioId)

        notifyListChange(_allAudio.value)
    }

    private fun notifyListChange(selectableAudioList: List<SelectableAudio>?) {
        val newList = selectableAudioList?.map { audioWithSelected ->
            SelectableAudio(
                audio = audioWithSelected.audio,
                selected = selectedAudioIds.any { it == audioWithSelected.audio.data }
            )
        } ?: emptyList()

        _allAudio.postValue(newList)

    }

    fun onDone() {
        val newAudioList = _allAudio.value?.filter {
            it.selected
        }?.map {
            it.audio
        } ?: return
        Log.e("updatedPlaylist", "newAudioList: $newAudioList")

        viewModelScope.launch {
            val updatedPlaylist = DBPlaylist(playlistName, newAudioList, false)
            Log.e("updatedPlaylist", "updatedPlaylist: ${updatedPlaylist.dbAudioList.size}")
            audioRepository.addPlayList(updatedPlaylist)
        }
    }


    init {
        viewModelScope.launch {
            val playlist = audioRepository.getSinglePlayListById(playlistName) ?: return@launch
            val currentPlaylistAudioList = playlist.dbAudioList.map { it.data }
            selectedAudioIds.clear()
            selectedAudioIds.addAll(currentPlaylistAudioList)
            Log.e("updatedPlaylist", "currentPlaylistAudioList: $currentPlaylistAudioList")
            searchAudios("")
        }
    }
}


