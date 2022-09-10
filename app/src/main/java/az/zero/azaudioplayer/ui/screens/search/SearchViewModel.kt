package az.zero.azaudioplayer.ui.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import az.zero.player.AudioRepository
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


    fun searchAudios(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _allAudio.postValue(audioRepository.getAllDbAudioSingleListByQuery(query))
        }
    }

    fun audioAction(action: AudioActions) {
        audioRepository.audioAction(action)
    }

    init {
        searchAudios("")
    }
}

