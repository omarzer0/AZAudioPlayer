package az.zero.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import az.zero.base.constants.FAVOURITE_PLAY_LIST_ID
import az.zero.base.utils.AlbumSortBy
import az.zero.base.utils.AlbumSortBy.ASCENDING
import az.zero.base.utils.AlbumSortBy.DESCENDING
import az.zero.base.utils.AudioSortBy
import az.zero.base.utils.AudioSortBy.*
import az.zero.db.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioDao {

    fun getAllDbAudio(
        sortBy: AudioSortBy,
    ): Flow<List<DBAudio>> =
        when (sortBy) {
            DATE_OF_UPDATE -> getAllAudioSortedByDateOfUpdate()
            AUDIO_NAME -> getAllAudioSortedByAudioName()
            ARTIST_NAME -> getAllAudioSortedByArtistName()
        }

    @Query("SELECT * FROM DBAudio ORDER BY lastDateModified DESC")
    fun getAllAudioSortedByDateOfUpdate(): Flow<List<DBAudio>>

    @Query("SELECT * FROM DBAudio ORDER BY title ASC")
    fun getAllAudioSortedByAudioName(): Flow<List<DBAudio>>

    @Query("SELECT * FROM DBAudio ORDER BY artist ASC")
    fun getAllAudioSortedByArtistName(): Flow<List<DBAudio>>

    @Query("DELETE FROM DBAudio WHERE data=:audioData")
    suspend fun deleteAudioById(audioData: String)

    fun getAlbumWithAudio(albumSortOrder: AlbumSortBy): Flow<List<DBAlbumWithAudioList>> =
        when (albumSortOrder) {
            ASCENDING -> getAlbumWithAudioSortedASC()
            DESCENDING -> getAlbumWithAudioSortedDESC()
        }

    @Transaction
    @Query("SELECT * FROM DBAlbum ORDER BY name ASC")
    fun getAlbumWithAudioSortedASC(): Flow<List<DBAlbumWithAudioList>>

    @Transaction
    @Query("SELECT * FROM DBAlbum ORDER BY name DESC")
    fun getAlbumWithAudioSortedDESC(): Flow<List<DBAlbumWithAudioList>>


    @Query("SELECT * FROM DBAudio ORDER BY lastDateModified DESC")
    suspend fun getAllDbAudioSingleList(): List<DBAudio>

    @Query("SELECT * FROM DBAudio WHERE title LIKE '%' || :searchQuery || '%' OR album LIKE '%' || :searchQuery || '%' OR artist LIKE '%' || :searchQuery || '%' ORDER BY lastDateModified DESC ")
    suspend fun getAllDbAudioSingleListByQuery(searchQuery: String): List<DBAudio>?

    @Transaction
    @Query("SELECT * FROM DBArtist")
    fun getArtistWithAudio(): LiveData<List<DBArtistWithAudios>>

    @Query("SELECT * FROM DBPlaylist ORDER BY isFavouritePlaylist DESC")
    fun getAllPlayLists(): LiveData<List<DBPlaylist>>

    @Query("SELECT * FROM DBPlaylist WHERE isFavouritePlaylist = 0")
    suspend fun getAllPlayListsWithoutFavouritePlaylistOnce(): List<DBPlaylist>

    @Query("SELECT * FROM DBPlaylist WHERE isFavouritePlaylist = 0")
    fun getAllPlayListsWithoutFavouritePlaylist(): LiveData<List<DBPlaylist>>

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

    @Query("SELECT * FROM DBPlaylist WHERE id=:playlistId")
    suspend fun getSinglePlaylistById(playlistId: Long): DBPlaylist?

    @Query("SELECT * FROM DBPlaylist WHERE id=:playlistId")
    fun getPlaylistById(playlistId: Long): LiveData<DBPlaylist>

    @Query("DELETE FROM DBPlaylist WHERE id=:playlistId")
    suspend fun deletePlaylistById(playlistId: Long)

    @Query("UPDATE DBPlaylist SET name=:playlistName WHERE id=:playlistId")
    suspend fun onUpdatePlaylistName(playlistName: String, playlistId: Long)

    @Insert(onConflict = REPLACE)
    suspend fun addPlayList(DBPlaylist: DBPlaylist): Long

    @Query("UPDATE DBAudio SET isFavourite = 0 WHERE isFavourite = 1")
    suspend fun unFavAllFavAudio()

    @Transaction
    suspend fun clearFavList(context: Context) {
        unFavAllFavAudio()

        addPlayList(
            DBPlaylist(
                id = FAVOURITE_PLAY_LIST_ID,
                name = context.getString(az.zero.base.R.string.favourites),
                dbAudioList = emptyList(),
                isFavouritePlaylist = true
            )
        )
    }
}