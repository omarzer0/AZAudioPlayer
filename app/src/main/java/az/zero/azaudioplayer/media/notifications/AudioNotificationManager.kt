package az.zero.azaudioplayer.media.notifications

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import az.zero.azaudioplayer.R
import com.bumptech.glide.RequestManager
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.*
import javax.inject.Inject

class AudioNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val notificationManager: PlayerNotificationManager

    @Inject
    lateinit var glideManager: RequestManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            NOTIFICATION_CHANNEL_ID,
            R.string.notification_channel_name,
            R.string.notification_channel_description,
            NOTIFICATION_ID,
            DescriptionAdapter(mediaController),
            notificationListener
        ).apply {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_music)

            setRewindIncrementMs(0)
            setFastForwardIncrementMs(0)
        }
    }

    fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    private inner class DescriptionAdapter(private val mediaController: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun getCurrentContentTitle(player: Player): CharSequence =
            mediaController.metadata.description.title?.toString() ?: ""

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            mediaController.sessionActivity

        override fun getCurrentContentText(player: Player): CharSequence? =
            mediaController.metadata.description.subtitle?.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
//            val iconUri = mediaController.metadata.description.iconUri
//            return if (currentIconUri != iconUri || currentBitmap == null) {
//                currentIconUri = iconUri
//                serviceScope.launch {
//                    currentBitmap = iconUri?.let {
//                        resolveUriAsBitmap(it)
//                    }
//                    currentBitmap?.let { callback.onBitmap(it) }
//                }
//                null
//            } else {
//                currentBitmap
//            }
            return null
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                // Block on downloading artwork.
                glideManager
                    .asBitmap().load(uri)
                    .submit(NOTIFICATION_LARGE_ICON_SIZE, NOTIFICATION_LARGE_ICON_SIZE)
                    .get()
            }
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "AZ play"
        const val NOTIFICATION_ID = 1015
        const val NOTIFICATION_LARGE_ICON_SIZE = 144
    }
}

