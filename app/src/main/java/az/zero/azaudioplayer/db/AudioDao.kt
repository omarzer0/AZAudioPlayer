package az.zero.azaudioplayer.db

import androidx.lifecycle.LiveData
import androidx.room.*
import az.zero.azaudioplayer.db.entities.DBAlbum
import az.zero.azaudioplayer.db.entities.DBAlbumWithAudios
import az.zero.azaudioplayer.db.entities.DBAudio

@Dao
interface AudioDao {

    @Query("SELECT * FROM DBAudio ORDER BY lastDateModified DESC")
    fun getAllDbAudio(): LiveData<List<DBAudio>>

    @Query("SELECT * FROM DBAudio ORDER BY lastDateModified DESC")
    suspend fun getAllDbAudioSingleList(): List<DBAudio>

    @Transaction
    @Query("SELECT * FROM DBAlbum")
    fun getAlbumWithAudio(): LiveData<List<DBAlbumWithAudios>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dbAudio: DBAudio): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dbAlbum: DBAlbum): Long

    @Delete
    suspend fun delete(dbAudio: DBAudio)

    @Query("Delete From DBAlbum")
    suspend fun deleteAllAlbums()


}