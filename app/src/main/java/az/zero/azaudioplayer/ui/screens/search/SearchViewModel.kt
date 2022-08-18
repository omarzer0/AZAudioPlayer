package az.zero.azaudioplayer.ui.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.data.db.AudioDao
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.domain.use_case.AudioActionUseCase
import az.zero.azaudioplayer.utils.AudioActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val audioDao: AudioDao,
    private val audioActionUseCase: AudioActionUseCase
) : ViewModel() {

    private val _allAudio = MutableLiveData<List<Audio>>()
    val allAudio: LiveData<List<Audio>> = _allAudio

    fun searchAudios(query: String) {
        viewModelScope.launch {
            _allAudio.postValue(audioDao.getAllDbAudioSingleListByQuery(query))
        }
    }

    fun audioAction(action: AudioActions) {
        audioActionUseCase(action)
    }

    init {
        searchAudios("")
    }
}

