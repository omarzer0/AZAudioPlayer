package az.zero.azaudioplayer.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import az.zero.azaudioplayer.domain.models.*

@Dao
interface AudioDao {

    @Query("SELECT * FROM Audio ORDER BY lastDateModified DESC")
    fun getAllDbAudio(): LiveData<List<Audio>>

    @Query("SELECT * FROM Audio ORDER BY lastDateModified DESC")
    suspend fun getAllDbAudioSingleList(): List<Audio>

    @Transaction
    @Query("SELECT * FROM DBAlbum")
    fun getAlbumWithAudio(): LiveData<List<Album>>

    @Transaction
    @Query("SELECT * FROM DBArtist")
    fun getArtistWithAudio(): LiveData<List<Artist>>

    @Query("SELECT * FROM Playlist")
    fun getAllPlayLists(): LiveData<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audio: Audio): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dbAlbum: DBAlbum): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artist: DBArtist): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlayList(playlist: Playlist)

    @Delete
    suspend fun delete(audio: Audio)

    @Query("Delete From DBAlbum")
    suspend fun deleteAllAlbums()

    @Query("Delete From DBArtist")
    suspend fun deleteAllArtists()

    @Query("SELECT * FROM Audio WHERE data =:audioData")
    suspend fun getAudioById(audioData: String): Audio?


}