package az.zero.azaudioplayer.ui

import androidx.lifecycle.ViewModel
import az.zero.azaudioplayer.db.AudioDao
import az.zero.azaudioplayer.media.player.AudioServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection,
    private val audioDao: AudioDao
) : ViewModel() {

    fun play(audioDataId: String) {
        audioServiceConnection.playPauseOrToggle(audioDataId)
    }

    val allAudio = audioDao.getAllDbAudio()

}