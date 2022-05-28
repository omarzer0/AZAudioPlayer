package az.zero.azaudioplayer.db

import androidx.lifecycle.LiveData
import androidx.room.*
import az.zero.azaudioplayer.data.models.Album
import az.zero.azaudioplayer.data.models.Audio
import az.zero.azaudioplayer.data.models.DBAlbum

@Dao
interface AudioDao {

    @Query("SELECT * FROM Audio ORDER BY lastDateModified DESC")
    fun getAllDbAudio(): LiveData<List<Audio>>

    @Query("SELECT * FROM Audio ORDER BY lastDateModified DESC")
    suspend fun getAllDbAudioSingleList(): List<Audio>

    @Transaction
    @Query("SELECT * FROM DBAlbum")
    fun getAlbumWithAudio(): LiveData<List<Album>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(audio: Audio): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dbAlbum: DBAlbum): Long

    @Delete
    suspend fun delete(audio: Audio)

    @Query("Delete From DBAlbum")
    suspend fun deleteAllAlbums()


}