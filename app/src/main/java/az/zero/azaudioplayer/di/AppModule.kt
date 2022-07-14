package az.zero.azaudioplayer.di

import android.content.Context
import androidx.room.Room
import az.zero.azaudioplayer.data.db.AppDatabase
import az.zero.azaudioplayer.data.db.AppDatabase.Companion.DATABASE_NAME
import az.zero.azaudioplayer.data.db.AudioDao
import az.zero.azaudioplayer.data.db.DatabaseCallback
import az.zero.azaudioplayer.data.db.helpers.AudioTypeConverters
import az.zero.azaudioplayer.media.player.AudioServiceConnection

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
        @ApplicationContext context: Context,
        dao: AudioDao,
        @ApplicationScope scope: CoroutineScope
    ) = AudioServiceConnection(context, dao, scope)

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addTypeConverter(AudioTypeConverters())
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

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

