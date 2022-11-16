package az.zero.azaudioplayer.ui.screens.search

import androidx.lifecycle.*
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    private val _allAudio = MutableLiveData<List<DBAudio>>()
    val allDBAudio: LiveData<List<DBAudio>> = _allAudio
    private var searchJob: Job? = null

    val currentPlayingAudio = audioRepository.nowPlayingDBAudio.distinctUntilChanged()

    fun searchAudios(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val filteredList = audioRepository.getAllDbAudioSingleListByQuery(query) ?: emptyList()
            _allAudio.postValue(filteredList)
        }
    }

    fun audioAction(action: AudioActions, newAudioList: List<DBAudio>?) {
        audioRepository.audioAction(action, newAudioList = newAudioList)
    }

    init {
        searchAudios("")
    }
}

