package az.zero.azaudioplayer.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    val name: String,
    val audioList: List<Audio>,
    @PrimaryKey val id: Long? = null
)


