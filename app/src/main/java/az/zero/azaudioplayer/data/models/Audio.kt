package az.zero.azaudioplayer.data.models

import android.os.Parcelable
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Audio(
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
) : Parcelable

fun Audio.toMediaMetadataCompat(): MediaMetadataCompat {
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

fun Audio.toMediaItem(): MediaBrowserCompat.MediaItem {
    val desc = MediaDescriptionCompat.Builder()
        .setMediaUri(data.toUri())
        .setTitle(title)
        .setSubtitle(displayName)
        .setMediaId(data)
        .setIconUri(cover.toUri())
        .build()
    return MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
}

fun Audio.toExoMediaItem(): MediaItem {
    return MediaItem.Builder()
        .setMediaId(data)
        .setUri(data.toUri())
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setMediaUri(data.toUri())
                .setTitle(title)
                .setSubtitle(displayName)
                .setArtist(artist)
                .setAlbumTitle(album)
                .setDescription(displayName)
                .build()
        ).build()
}


