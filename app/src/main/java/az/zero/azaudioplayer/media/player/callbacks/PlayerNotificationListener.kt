package az.zero.azaudioplayer.media.player.callbacks

import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import az.zero.azaudioplayer.media.player.AudioNotificationManager.Companion.NOTIFICATION_ID
import az.zero.azaudioplayer.media.service.AudioService
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class PlayerNotificationListener(
    private val audioService: AudioService
) : PlayerNotificationManager.NotificationListener {

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        audioService.apply {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                startForeground(NOTIFICATION_ID, notification)
                isForegroundService = true
            }
        }
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        audioService.apply {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }
}