package az.zero.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class DBPlaylist(
    val name: String,
    val dbAudioList: List<DBAudio>,
    val isFavouritePlaylist: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id:Long? = null,
) : Parcelable


