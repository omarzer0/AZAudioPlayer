package az.zero.azaudioplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AZApplication:Application(){


    override fun onCreate() {
        super.onCreate()
    }

}