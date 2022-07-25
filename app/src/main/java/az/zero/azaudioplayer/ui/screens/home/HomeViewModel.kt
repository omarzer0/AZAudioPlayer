package az.zero.azaudioplayer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import az.zero.azaudioplayer.data.db.AudioDao
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.media.player.AudioServiceConnection
import az.zero.azaudioplayer.ui.screens.home.AudioActions.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection,
    private val audioDao: AudioDao
) : ViewModel() {

    fun audioAction(action: AudioActions) {
        audioServiceConnection.audioAction(action)
    }

    val currentPlayingAudio = audioServiceConnection.nowPlayingAudio.distinctUntilChanged()
    val playbackState = audioServiceConnection.playbackState

    val allAudio by lazy { audioDao.getAllDbAudio().distinctUntilChanged() }

    val allAlbums by lazy { audioDao.getAlbumWithAudio().distinctUntilChanged() }

    val allArtists by lazy { audioDao.getArtistWithAudio().distinctUntilChanged() }

    val allPlaylists by lazy { audioDao.getAllPlayLists().distinctUntilChanged() }

    fun addOrRemoveFromFavourite(audio: Audio) {
        viewModelScope.launch {
            audioDao.addOrRemoveFromFavouritePlayList(audio)
        }
    }

    fun playOrPause() {
        audioServiceConnection.playOrPause()
    }
}

sealed class AudioActions {
    object Play : AudioActions()
    object Pause : AudioActions()
    data class Toggle(val audioDataId: String) : AudioActions()
}

