package az.zero.azaudioplayer.media.player.callbacks

import az.zero.azaudioplayer.media.notifications.AudioNotificationManager
import az.zero.azaudioplayer.media.service.AudioService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

class PlayerEventListener(
    private val audioService: AudioService,
    private val notificationManager: AudioNotificationManager,
    private val player: ExoPlayer
) : Player.EventListener {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        super.onPlayerStateChanged(playWhenReady, playbackState)
        when (playbackState) {
            Player.STATE_BUFFERING,
            Player.STATE_READY -> {
                notificationManager.showNotification(player)
                // If playback is paused we remove the foreground state which allows the
                // notification to be dismissed. An alternative would be to provide a "close"
                // button in the notification which stops playback and clears the notification.
                if (playbackState == Player.STATE_READY) {
                    if (!playWhenReady) audioService.stopForeground(false)
                }
            }
            else -> {
                notificationManager.hideNotification()
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        // Handle error
    }
}