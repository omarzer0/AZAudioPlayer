package az.zero.db.di

import android.content.Context
import androidx.room.Room
import az.zero.base.BuildConfig
import az.zero.db.AppDatabase
import az.zero.db.AudioDao
import az.zero.db.DatabaseCallback
import az.zero.db.helpers.AudioTypeConverters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModules {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .apply {
                addTypeConverter(AudioTypeConverters())
                addCallback(callback)
                if (BuildConfig.DEBUG) {
                    fallbackToDestructiveMigration()
                }
            }
            .build()

    @Singleton
    @Provides
    fun providesAudioDao(appDatabase: AppDatabase): AudioDao = appDatabase.getAudioDao()
}
