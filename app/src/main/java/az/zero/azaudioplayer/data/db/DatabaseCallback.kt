package az.zero.azaudioplayer.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import az.zero.azaudioplayer.data.db.helpers.AudioDbHelper
import javax.inject.Inject
import javax.inject.Provider

class DatabaseCallback @Inject constructor(
    private val database: Provider<AppDatabase>,
    private val audioDbHelper: AudioDbHelper
) : RoomDatabase.Callback() {

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        val dao = database.get().getAudioDao()
        audioDbHelper.compareWithLocalList(dao)
    }

//    override fun onCreate(db: SupportSQLiteDatabase) {
//        super.onCreate(db)
//        val dao = database.get().getAudioDao()
//        audioDbHelper.createFavouritePlaylist(dao)
//    }
}