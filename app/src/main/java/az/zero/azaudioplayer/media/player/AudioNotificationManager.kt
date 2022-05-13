package az.zero.azaudioplayer.media.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import az.zero.azaudioplayer.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import kotlinx.coroutines.*

class AudioNotificationManager(
    private val context: Context,
    private val sessionToken: MediaSessionCompat.Token,
    private val notificationListener: PlayerNotificationManager.NotificationListener,
) {
    private val mediaController = MediaControllerCompat(context, sessionToken)

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private val notificationManager: PlayerNotificationManager = PlayerNotificationManager.Builder(
        context,
        NOTIFICATION_ID,
        NOTIFICATION_CHANNEL_ID
    )
        .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
        .setNotificationListener(notificationListener)
        .build().apply {
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_music)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
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
            val iconUri = mediaController.metadata.description.iconUri
            return if (currentIconUri != iconUri || currentBitmap == null) {
                currentIconUri = iconUri
                scope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                // Block on downloading artwork.
                try {
                    Glide.with(context).applyDefaultRequestOptions(glideOptions)
                        .asBitmap().load(uri)
                        .submit()
                        .get()
                }catch (e:Exception){
                    null
                }
            }
        }
    }

    private val glideOptions = RequestOptions()
        .fallback(R.drawable.ic_image)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "AZ play"
        const val NOTIFICATION_ID = 1015
        const val NOTIFICATION_LARGE_ICON_SIZE = 144
    }
}

