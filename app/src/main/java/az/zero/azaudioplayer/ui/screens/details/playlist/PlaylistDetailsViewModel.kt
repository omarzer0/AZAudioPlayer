package az.zero.azaudioplayer.ui.screens.details.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val stateHandler: SavedStateHandle
) : ViewModel() {
    val currentPlayingAudio = audioRepository.nowPlayingDBAudio.distinctUntilChanged()
    private val playlistName = stateHandler.get<String>("playlistName") ?: ""

    val playlist = audioRepository.getPlaylistById(playlistName)

    fun audioAction(action: AudioActions, newAudioList: List<DBAudio>?) {
        audioRepository.audioAction(action, newAudioList = newAudioList)
    }

    fun deleteCurrentPlayList() {
        viewModelScope.launch {
            audioRepository.deletePlayListById(playlistName)
        }
    }

    fun clearFavList() {
        viewModelScope.launch {
            audioRepository.clearFavList()
            audioRepository.getUpdatedCurrentlyPlaying(currentPlayingAudio.value?.data ?: "")
        }
    }
}