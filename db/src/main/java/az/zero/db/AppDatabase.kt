package az.zero.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import az.zero.db.entities.DBAlbum
import az.zero.db.entities.DBArtist
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import az.zero.db.helpers.AudioTypeConverters

@Database(
    entities = [DBAudio::class, DBAlbum::class, DBArtist::class, DBPlaylist::class],
    version = 1
)
@TypeConverters(AudioTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAudioDao(): AudioDao

    companion object {
        const val DATABASE_NAME = "Az audio player db"
    }
}