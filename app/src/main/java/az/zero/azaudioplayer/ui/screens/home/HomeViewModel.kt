package az.zero.azaudioplayer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val audioRepository: AudioRepository,
) : ViewModel() {

    fun audioAction(action: AudioActions, newAudioList: List<DBAudio>?) {
        audioRepository.audioAction(action, newAudioList = newAudioList)
    }

    val currentPlayingAudio = audioRepository.nowPlayingDBAudio.distinctUntilChanged()
    val playbackState = audioRepository.playbackState

    val allAudio = audioRepository.getAllAudio()

    val allAlbums by lazy { audioRepository.getAlbumWithAudio() }

    val allArtists by lazy { audioRepository.getArtistWithAudio() }

    val allPlaylists = audioRepository.getAllPlayLists().distinctUntilChanged()

    fun addOrRemoveFromFavourite(DBAudio: DBAudio) {
        viewModelScope.launch {
            audioRepository.addOrRemoveFromFavouritePlayList(DBAudio)
        }
    }

    fun playOrPause() {
        audioRepository.playOrPause()
    }

    fun createANewPlayList(playlistName: String) {
        viewModelScope.launch {
            audioRepository.addPlayList(
                DBPlaylist(
                    name = playlistName,
                    emptyList()
                )
            )
        }
    }
}

