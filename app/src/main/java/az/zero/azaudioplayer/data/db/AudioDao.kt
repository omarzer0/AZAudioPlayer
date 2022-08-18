package az.zero.azaudioplayer.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import az.zero.azaudioplayer.domain.models.*

@Dao
interface AudioDao {

    @Query("SELECT * FROM Audio ORDER BY lastDateModified DESC")
    fun getAllDbAudio(): LiveData<List<Audio>>

    @Query("SELECT * FROM Audio ORDER BY lastDateModified DESC")
    suspend fun getAllDbAudioSingleList(): List<Audio>

    @Query("SELECT * FROM Audio WHERE title LIKE '%' || :searchQuery || '%' OR album LIKE '%' || :searchQuery || '%' OR artist LIKE '%' || :searchQuery || '%' ORDER BY lastDateModified DESC ")
    suspend fun getAllDbAudioSingleListByQuery(searchQuery: String): List<Audio>

    @Transaction
    @Query("SELECT * FROM DBAlbum")
    fun getAlbumWithAudio(): LiveData<List<Album>>

    @Transaction
    @Query("SELECT * FROM DBArtist")
    fun getArtistWithAudio(): LiveData<List<Artist>>

    @Query("SELECT * FROM Playlist ORDER BY isFavouritePlaylist DESC")
    fun getAllPlayLists(): LiveData<List<Playlist>>

    @Query("SELECT * FROM Playlist WHERE isFavouritePlaylist = 0")
    suspend fun getAllPlayListsWithoutFavouritePlaylist(): List<Playlist>

    @Query("SELECT * FROM PLAYLIST WHERE isFavouritePlaylist = 1 ")
    suspend fun getFavouritePlaylist(): List<Playlist>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audio: Audio): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dbAlbum: DBAlbum): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artist: DBArtist): Long

    @Update(onConflict = REPLACE)
    suspend fun updateAudio(audio: Audio): Int

    suspend fun addOrRemoveFromFavouritePlayList(audio: Audio) {
        val favPlaylist = getFavouritePlaylist().firstOrNull() ?: return
        val favPlaylistAudios = favPlaylist.audioList.toMutableList()
        if (audio.isFavourite) {
            val audioToRemove = favPlaylistAudios.firstOrNull { it.data == audio.data }
            val removed = favPlaylistAudios.remove(audioToRemove)
        } else favPlaylistAudios.add(audio)

        updateAudio(audio.copy(isFavourite = !audio.isFavourite))
        addPlayList(
            favPlaylist.copy(
                audioList = favPlaylistAudios,
            )
        )
    }

    @Insert(onConflict = REPLACE)
    suspend fun addPlayList(playlist: Playlist): Long

    @Delete
    suspend fun delete(audio: Audio)

    @Query("Delete From DBAlbum")
    suspend fun deleteAllAlbums()

    @Query("Delete From DBArtist")
    suspend fun deleteAllArtists()

    @Query("DELETE FROM Playlist WHERE isFavouritePlaylist = 0")
    suspend fun deleteAllPlaylistsWithoutFavourite()

    @Query("DELETE FROM Playlist WHERE isFavouritePlaylist = 1")
    suspend fun deleteFavouritePlaylist()

    @Query("SELECT * FROM Audio WHERE data =:audioData")
    suspend fun getAudioById(audioData: String): Audio?

}