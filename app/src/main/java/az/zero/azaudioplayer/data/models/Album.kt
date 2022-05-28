package az.zero.azaudioplayer.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DBAlbum(
    @PrimaryKey
    val name: String
)

data class Album(
    @Embedded val album: DBAlbum,
    @Relation(
        parentColumn = "name",
        entityColumn = "album"
    )
    val audioList: List<Audio>
)