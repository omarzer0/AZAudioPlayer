package az.zero.azaudioplayer.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DBAudio(
    @PrimaryKey
    val data: String,
    val title: String,
    val artist: String,
    val lastDateModified: Long,
    val displayName: String,
    val album: String,
    val year: String,
    val cover: String,
    val id: Long? = null,
)

@Entity
data class DBAlbum(
    @PrimaryKey
    val name: String
)

data class DBAlbumWithAudios(
    @Embedded val album: DBAlbum,
    @Relation(
        parentColumn = "name",
        entityColumn = "album"
    )
    val audioList: List<DBAudio>
)