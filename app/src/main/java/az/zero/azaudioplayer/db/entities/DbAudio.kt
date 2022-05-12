package az.zero.azaudioplayer.db.entities

import android.support.v4.media.MediaMetadataCompat
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DBAudio(
    @PrimaryKey
    val data: String,
    val title: String,
    val artist: String,
    val lastDateModified: Long,
    val displayName: String,
    val album: String,
    val year: String,
    val cover: String,
    val id: Long? = null,
)

fun DBAudio.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, data)
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, cover)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, data)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cover)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, displayName)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, displayName)
        .build()
}

@Entity
data class DBAlbum(
    @PrimaryKey
    val name: String
)

data class DBAlbumWithAudios(
    @Embedded val album: DBAlbum,
    @Relation(
        parentColumn = "name",
        entityColumn = "album"
    )
    val audioList: List<DBAudio>
)