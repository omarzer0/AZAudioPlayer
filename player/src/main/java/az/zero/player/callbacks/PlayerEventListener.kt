package az.zero.player.callbacks

import az.zero.player.service.AudioService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player

class PlayerEventListener(
    private val audioService: AudioService,
    private val notificationManager: AudioNotificationManager,
    private val player: ExoPlayer
) : Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            Player.STATE_BUFFERING,
            Player.STATE_READY -> {
                notificationManager.showNotification(player)
                // If playback is paused we remove the foreground state which allows the
                // notification to be dismissed. An alternative would be to provide a "close"
                // button in the notification which stops playback and clears the notification.
                if (playbackState == Player.STATE_READY) {
                    audioService.stopForeground(false)
                }
            }
            else -> {
                notificationManager.hideNotification()
            }
        }
    }

}