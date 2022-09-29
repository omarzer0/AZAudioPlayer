package az.zero.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class DBPlaylist(
    @PrimaryKey
    val name: String,
    val DBAudioList: List<DBAudio>,
    val isFavouritePlaylist: Boolean = false,
) : Parcelable


