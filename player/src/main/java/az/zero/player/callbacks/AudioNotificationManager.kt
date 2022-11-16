package az.zero.player.callbacks

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import az.zero.base.constants.NOTIFICATION_CHANNEL_ID
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class AudioNotificationManager(
    private val context: Context,
    private val sessionToken: MediaSessionCompat.Token,
    private val notificationListener: PlayerNotificationManager.NotificationListener,
    private val pendingIntent: PendingIntent?,
) {
    private val mediaController = MediaControllerCompat(context, sessionToken)

    // TODO y
    private val notificationManager: PlayerNotificationManager = PlayerNotificationManager.Builder(
        context,
        NOTIFICATION_ID,
        NOTIFICATION_CHANNEL_ID
    )
        .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
        .setNotificationListener(notificationListener)
        .build().apply {
            setSmallIcon(az.zero.base.R.drawable.ic_music)
            setMediaSessionToken(sessionToken)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setUseNextActionInCompactView(true)
            setUsePreviousActionInCompactView(true)
        }


    fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    companion object {
        const val NOTIFICATION_ID = 1015
    }

    private inner class DescriptionAdapter(private val mediaController: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        override fun getCurrentContentTitle(player: Player): CharSequence =
            mediaController.metadata.description.title?.toString() ?: ""

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            pendingIntent

        override fun getCurrentContentText(player: Player): CharSequence? =
            mediaController.metadata.description.subtitle?.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? = null
    }
}

