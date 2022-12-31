package az.zero.azaudioplayer.di

import android.content.Context
import az.zero.azaudioplayer.AudioRepository
import az.zero.base.di.ApplicationScope
import az.zero.datastore.DataStoreManager
import az.zero.db.AudioDao
import az.zero.player.audio_data_source.AudioDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context,
        dao: AudioDao,
        @ApplicationScope scope: CoroutineScope,
        dataStoreManager: DataStoreManager,
        audioDataSource: AudioDataSource
    ) = AudioRepository(context, dao, scope, dataStoreManager, audioDataSource)

}
