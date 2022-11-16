package az.zero.azaudioplayer.ui.screens.home

import androidx.lifecycle.*
import az.zero.azaudioplayer.AudioRepository
import az.zero.azaudioplayer.utils.fakeAudio
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    val allPlaylists by lazy { audioRepository.getAllPlayLists() }

    fun addOrRemoveFromFavourite(DBAudio: DBAudio) {
        viewModelScope.launch {
            audioRepository.addOrRemoveFromFavouritePlayList(DBAudio)
        }
    }

    fun playOrPause() {
        audioRepository.playOrPause()
    }

    private val _errorFlow = MutableSharedFlow<Boolean>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun createANewPlayListIfNotExist(playlistName: String) {
        viewModelScope.launch {
            val exist = audioRepository.getSinglePlayListById(playlistName) != null
            _errorFlow.emit(false)
            if (!exist) {
                audioRepository.addPlayList(DBPlaylist(name = playlistName, emptyList()))
            } else {
                _errorFlow.emit(true)
            }

        }
    }
}

