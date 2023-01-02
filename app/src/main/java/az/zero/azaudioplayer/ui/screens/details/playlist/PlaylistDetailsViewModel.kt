package az.zero.azaudioplayer.ui.screens.details.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.utils.AudioActions
import az.zero.base.utils.PlayingListFrom
import az.zero.db.entities.DBAudio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
    private val stateHandler: SavedStateHandle,
) : ViewModel() {
    val currentPlayingAudio = audioRepository.nowPlayingDBAudio.distinctUntilChanged()
    val playbackState = audioRepository.playbackState

//    private val playlistName = stateHandler.get<String>("playlistName") ?: ""
    private val playlistId = stateHandler.get<Long>("playlistId") ?: 0L

    val playlist = audioRepository.getPlaylistById(playlistId)

    fun addOrRemoveFromFavourite(DBAudio: DBAudio) {
        viewModelScope.launch {
            audioRepository.addOrRemoveFromFavouritePlayList(DBAudio)
        }
    }

    fun playOrPause() {
        audioRepository.playOrPause()
    }

    fun audioAction(action: AudioActions, newAudioList: List<DBAudio>?) {
        audioRepository.audioAction(
            action = action,
            newAudioList = newAudioList,
            playingListFrom = PlayingListFrom.PLAYLIST
        )
    }

    fun deleteCurrentPlayList() {
        viewModelScope.launch {
            audioRepository.deletePlayListById(playlistId)
        }
    }

    fun clearFavList() {
        viewModelScope.launch {
            audioRepository.clearFavList()
            audioRepository.getUpdatedCurrentlyPlaying(currentPlayingAudio.value?.data ?: "")
        }
    }

    fun onUpdatePlaylistName(playlistName: String, playlistId: Long) {
        viewModelScope.launch {
            audioRepository.onUpdatePlaylistName(playlistName,playlistId)
        }
    }
}