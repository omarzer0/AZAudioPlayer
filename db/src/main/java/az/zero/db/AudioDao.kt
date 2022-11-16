package az.zero.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import az.zero.db.entities.*

@Dao
interface AudioDao {

    @Query("SELECT * FROM DBAudio ORDER BY lastDateModified DESC")
    fun getAllDbAudio(): LiveData<List<DBAudio>>

    @Query("SELECT * FROM DBAudio ORDER BY lastDateModified DESC")
    suspend fun getAllDbAudioSingleList(): List<DBAudio>

    @Query("SELECT * FROM DBAudio WHERE title LIKE '%' || :searchQuery || '%' OR album LIKE '%' || :searchQuery || '%' OR artist LIKE '%' || :searchQuery || '%' ORDER BY lastDateModified DESC ")
    suspend fun getAllDbAudioSingleListByQuery(searchQuery: String): List<DBAudio>?

    @Transaction
    @Query("SELECT * FROM DBAlbum")
    fun getAlbumWithAudio(): LiveData<List<DBAlbumWithAudioList>>

    @Transaction
    @Query("SELECT * FROM DBArtist")
    fun getArtistWithAudio(): LiveData<List<DBArtistWithAudios>>

    @Query("SELECT * FROM DBPlaylist ORDER BY isFavouritePlaylist DESC")
    fun getAllPlayLists(): LiveData<List<DBPlaylist>>

    @Query("SELECT * FROM DBPlaylist WHERE isFavouritePlaylist = 0")
    suspend fun getAllPlayListsWithoutFavouritePlaylist(): List<DBPlaylist>

    @Query("SELECT * FROM DBPlaylist WHERE isFavouritePlaylist = 1 ")
    suspend fun getFavouritePlaylist(): List<DBPlaylist>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(DBAudio: DBAudio): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dbAlbum: DBAlbum): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artist: DBArtist): Long

    @Update(onConflict = REPLACE)
    suspend fun updateAudio(DBAudio: DBAudio): Int

    suspend fun addOrRemoveFromFavouritePlayList(dbAudio: DBAudio) {
        val favPlaylist = getFavouritePlaylist().firstOrNull() ?: return
        val favPlaylistAudios = favPlaylist.dbAudioList.toMutableList()
        if (dbAudio.isFavourite) {
            val audioToRemove = favPlaylistAudios.firstOrNull { it.data == dbAudio.data }
            favPlaylistAudios.remove(audioToRemove)
        } else favPlaylistAudios.add(dbAudio)

        updateAudio(dbAudio.copy(isFavourite = !dbAudio.isFavourite))
        addPlayList(
            favPlaylist.copy(
                dbAudioList = favPlaylistAudios,
            )
        )
    }

    @Delete
    suspend fun delete(DBAudio: DBAudio)

    @Query("Delete From DBAlbum")
    suspend fun deleteAllAlbums()

    @Query("Delete From DBArtist")
    suspend fun deleteAllArtists()

    @Query("DELETE FROM DBPlaylist WHERE isFavouritePlaylist = 0")
    suspend fun deleteAllPlaylistsWithoutFavourite()

    @Query("DELETE FROM DBPlaylist WHERE isFavouritePlaylist = 1")
    suspend fun deleteFavouritePlaylist()

    @Query("SELECT * FROM DBAudio WHERE data =:audioData")
    suspend fun getAudioById(audioData: String): DBAudio?

    @Query("SELECT * FROM DBAudio WHERE data =:audioData")
    fun getAudioLiveDataById(audioData: String): LiveData<DBAudio>

    @Query("SELECT * FROM DBPlaylist WHERE name=:playlistId")
    suspend fun getSinglePlaylistById(playlistId: String): DBPlaylist?

    @Query("SELECT * FROM DBPlaylist WHERE name=:playlistId")
    fun getPlaylistById(playlistId: String): LiveData<DBPlaylist>

    @Query("DELETE FROM DBPlaylist WHERE name=:playlistId")
    suspend fun deletePlaylistById(playlistId: String)

    @Insert(onConflict = REPLACE)
    suspend fun addPlayList(DBPlaylist: DBPlaylist): Long

    @Query("UPDATE DBAudio SET isFavourite = 0 WHERE isFavourite = 1")
    suspend fun unFavAllFavAudio()

    @Transaction
    suspend fun clearFavList(context: Context) {
        unFavAllFavAudio()

        addPlayList(
            DBPlaylist(
                name = context.getString(az.zero.base.R.string.favourites),
                dbAudioList = emptyList(),
                isFavouritePlaylist = true
            )
        )


//        val allFavAudio = getFavouritePlaylist().firstOrNull() ?: return
//        Log.e("clearFavList", "clearFavList: $allFavAudio")
//        val newUnFavAudio = allFavAudio.DBAudioList.map {
//            it.copy(isFavourite = false)
//        }
//        newUnFavAudio.forEach { updateAudio(it) }
//        Log.e("clearFavList", "clearFavList: ${getFavouritePlaylist()}")

//        unFavAllFavAudio()
//        deleteFavouritePlaylist()
//        addPlayList(
//            DBPlaylist(
//                name = context.getString(az.zero.base.R.string.favourites),
//                DBAudioList = emptyList(),
//                isFavouritePlaylist = true
//            )
//        )

//        val favList = getFavouritePlaylist().firstOrNull()!!.DBAudioList
//        Log.e("clearFavList", "clearFavList: ${favList.size}")
//        unFavAllFavAudio()
////        deleteFavouritePlaylist()
//        val newFavList = getFavouritePlaylist().firstOrNull()!!.DBAudioList
//        Log.e("clearFavList", "clearFavList: ${newFavList.size}")
    }

}