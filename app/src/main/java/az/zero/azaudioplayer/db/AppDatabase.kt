package az.zero.azaudioplayer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import az.zero.azaudioplayer.db.entities.DBAlbum
import az.zero.azaudioplayer.db.entities.DBAudio

@Database(entities = [DBAudio::class, DBAlbum::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAudioDao(): AudioDao

    companion object {
        const val DATABASE_NAME = "Az audio player db"
    }
}