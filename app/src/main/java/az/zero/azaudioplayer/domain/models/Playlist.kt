package az.zero.azaudioplayer.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    @PrimaryKey
    val name: String,
    val audioList: List<Audio>,
    val isFavouritePlaylist: Boolean = false,
)


