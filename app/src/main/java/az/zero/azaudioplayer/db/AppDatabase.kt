package az.zero.azaudioplayer.db

import androidx.room.Database
import androidx.room.RoomDatabase
import az.zero.azaudioplayer.data.models.Audio
import az.zero.azaudioplayer.data.models.DBAlbum

@Database(entities = [Audio::class, DBAlbum::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAudioDao(): AudioDao

    companion object {
        const val DATABASE_NAME = "Az audio player db"
    }
}