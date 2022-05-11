package az.zero.azaudioplayer.di

import android.content.Context
import az.zero.azaudioplayer.R
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.C.WAKE_MODE_LOCAL
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    @ServiceScoped
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ) = SimpleExoPlayer.Builder(context).build().apply {
        setAudioAttributes(audioAttributes, true)
        setHandleAudioBecomingNoisy(true)
        setWakeMode(WAKE_MODE_LOCAL)
    }

    @ServiceScoped
    @Provides
    fun provideDataSourceFactory(
        @ApplicationContext context: Context
    ) = DefaultDataSourceFactory(
        context,
        Util.getUserAgent(context, context.getString(R.string.app_name))
    )
}












