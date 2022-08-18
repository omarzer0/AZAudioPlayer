package az.zero.azaudioplayer.domain.use_case

import az.zero.azaudioplayer.media.player.AudioServiceConnection
import az.zero.azaudioplayer.utils.AudioActions
import javax.inject.Inject

class AudioActionUseCase @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection,
) {
    operator fun invoke(action: AudioActions) {
        audioServiceConnection.audioAction(action)
    }
}