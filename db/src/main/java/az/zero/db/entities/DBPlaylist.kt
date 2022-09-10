package az.zero.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DBPlaylist(
    @PrimaryKey
    val name: String,
    val DBAudioList: List<DBAudio>,
    val isFavouritePlaylist: Boolean = false,
)


