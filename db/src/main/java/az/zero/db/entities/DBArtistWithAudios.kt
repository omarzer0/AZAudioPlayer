package az.zero.db.entities

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class DBArtist(
    @PrimaryKey
    val name: String,
) : Parcelable

@Parcelize
data class DBArtistWithAudios(
    @Embedded val artist: DBArtist,
    @Relation(
        parentColumn = "name",
        entityColumn = "artist"
    )
    val dbAudioList: List<DBAudio>,
) : Parcelable