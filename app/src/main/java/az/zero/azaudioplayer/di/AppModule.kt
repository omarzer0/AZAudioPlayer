package az.zero.azaudioplayer.di

import android.content.Context
import androidx.room.Room
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.db.AppDatabase
import az.zero.azaudioplayer.db.AppDatabase.Companion.DATABASE_NAME
import az.zero.azaudioplayer.db.AudioDao
import az.zero.azaudioplayer.media.connection.AudioServiceConnection

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context
    ) = AudioServiceConnection(context)

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .error(R.drawable.ic_image)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: AppDatabase.DatabaseCallback
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Singleton
    @Provides
    fun providesAudioDao(appDatabase: AppDatabase): AudioDao = appDatabase.getAudioDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

/** Defined a scope for coroutine as if later a different scope is used this
 *  will tell dagger that this is as long as the application lives and other
 *  scopes can be defend
 *  @author Omar Adel
 **/
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

