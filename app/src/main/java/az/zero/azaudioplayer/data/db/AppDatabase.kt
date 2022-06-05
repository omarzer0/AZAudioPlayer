package az.zero.azaudioplayer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import az.zero.azaudioplayer.data.db.helpers.AudioTypeConverters
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.domain.models.DBAlbum
import az.zero.azaudioplayer.domain.models.DBArtist
import az.zero.azaudioplayer.domain.models.Playlist

@Database(entities = [Audio::class, DBAlbum::class, DBArtist::class, Playlist::class], version = 1)
@TypeConverters(AudioTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAudioDao(): AudioDao

    companion object {
        const val DATABASE_NAME = "Az audio player db"
    }
}