package az.zero.azaudioplayer.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import az.zero.azaudioplayer.data.db.AudioDao
import az.zero.azaudioplayer.media.player.AudioServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection,
    private val audioDao: AudioDao
) : ViewModel() {

    fun play(audioDataId: String) {
        audioServiceConnection.playPauseOrToggle(audioDataId)
    }

    val allAudio by lazy { audioDao.getAllDbAudio().distinctUntilChanged() }

    val allAlbums by lazy { audioDao.getAlbumWithAudio().distinctUntilChanged() }

    val allArtists by lazy { audioDao.getArtistWithAudio().distinctUntilChanged() }

    val allPlaylists by lazy { audioDao.getAllPlayLists().distinctUntilChanged() }

}