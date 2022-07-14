package az.zero.azaudioplayer.domain.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DBArtist(
    @PrimaryKey
    val name: String
)

data class Artist(
    @Embedded val artist: DBArtist,
    @Relation(
        parentColumn = "name",
        entityColumn = "artist"
    )
    val audioList: List<Audio>
)