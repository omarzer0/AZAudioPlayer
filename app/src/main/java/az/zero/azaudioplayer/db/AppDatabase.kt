package az.zero.azaudioplayer.db

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import az.zero.azaudioplayer.db.audio_helper.AudioDbHelper
import az.zero.azaudioplayer.db.entities.DBAlbum
import az.zero.azaudioplayer.db.entities.DBAudio
import az.zero.azaudioplayer.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [DBAudio::class, DBAlbum::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAudioDao(): AudioDao

    class DatabaseCallback @Inject constructor(

        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope,
        private val audioDbHelper: AudioDbHelper
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)

            val dao = database.get().getAudioDao()
            applicationScope.launch {
                Log.e("getAllDbAudio", "inside launch")

                audioDbHelper.compareWithLocalList(
                    dao.getAllDbAudio().value ?: emptyList(),
                    listToDeleteFromDb = {
                        Log.e("getAllDbAudio", "delete $it")
                        it.forEach { audio -> dao.delete(audio) }
                    },
                    listToAddToDb = {
                        Log.e("getAllDbAudio", "add $it")
                        it.forEach { audio -> dao.insert(audio) }
                    }, listOfAlbums = {
                        dao.deleteAllAlbums()
                        it.forEach { album -> dao.insert(album) }
                    })
            }
        }
    }

    companion object {
        const val DATABASE_NAME = "Az audio player db"
    }
}