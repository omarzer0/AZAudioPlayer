package az.zero.db.entities

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class DBAlbum(
    @PrimaryKey
    val name: String,
) : Parcelable

@Parcelize
data class DBAlbumWithAudioList(
    @Embedded val album: DBAlbum,
    @Relation(
        parentColumn = "name",
        entityColumn = "album"
    )
    val dbAudioList: List<DBAudio>,
) : Parcelable