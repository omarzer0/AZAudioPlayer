package az.zero.azaudioplayer

import android.app.Application
import android.graphics.Color
import com.google.android.exoplayer2.util.NotificationUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AZApplication : Application() {
    var checkedForPermission = false

    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createNotificationChannel(
            this,
            NOTIFICATION_CHANNEL_ID,
            R.string.app_name,
            R.string.notification_channel_description,
            NotificationUtil.IMPORTANCE_LOW
        )
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel() {
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            CHANNEL_NAME,
//            NotificationManager.IMPORTANCE_HIGH
//        ).apply {
//            description = DESCRIPTION
//            enableLights(false)
//            lightColor = NOTIFICATION_LIGHT_COLOR
//            enableVibration(false)
//        }
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "AZ player"
        const val CHANNEL_ID = "AZ player channel id"
        const val CHANNEL_NAME = "AZ player audio channel"
        const val DESCRIPTION = "AZ player Channel"
        const val NOTIFICATION_LIGHT_COLOR = Color.GREEN
    }
}