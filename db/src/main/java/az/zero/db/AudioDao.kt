package az.zero.db

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

    suspend fun addOrRemoveFromFavouritePlayList(DBAudio: DBAudio) {
        val favPlaylist = getFavouritePlaylist().firstOrNull() ?: return
        val favPlaylistAudios = favPlaylist.DBAudioList.toMutableList()
        if (DBAudio.isFavourite) {
            val audioToRemove = favPlaylistAudios.firstOrNull { it.data == DBAudio.data }
            val removed = favPlaylistAudios.remove(audioToRemove)
        } else favPlaylistAudios.add(DBAudio)

        updateAudio(DBAudio.copy(isFavourite = !DBAudio.isFavourite))
        addPlayList(
            favPlaylist.copy(
                DBAudioList = favPlaylistAudios,
            )
        )
    }

    @Insert(onConflict = REPLACE)
    suspend fun addPlayList(DBPlaylist: DBPlaylist): Long

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

}