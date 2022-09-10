package az.zero.db.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DBArtist(
    @PrimaryKey
    val name: String
)

data class DBArtistWithAudios(
    @Embedded val artist: DBArtist,
    @Relation(
        parentColumn = "name",
        entityColumn = "artist"
    )
    val DBAudioList: List<DBAudio>
)