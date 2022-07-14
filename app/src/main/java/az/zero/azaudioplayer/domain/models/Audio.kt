package az.zero.azaudioplayer.domain.models

import android.os.Parcelable
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import androidx.room.Entity
import androidx.room.PrimaryKey
import az.zero.azaudioplayer.domain.models.Audio.Companion.DURATION
import az.zero.azaudioplayer.domain.models.Audio.Companion.IS_FAVOURITE
import az.zero.azaudioplayer.domain.models.Audio.Companion.LAST_MODIFIED
import az.zero.azaudioplayer.domain.models.Audio.Companion.YEAR
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
    val duration: Long,
    val isFavourite: Boolean = false,
    val id: Long? = null,
) : Parcelable {

    companion object {
        const val LAST_MODIFIED = "LAST_MODIFIED"
        const val YEAR = "YEAR"
        const val DURATION = "DURATION"
        const val IS_FAVOURITE = "IS_FAVOURITE"
    }
}


fun Audio.toMediaMetadataCompat(): MediaMetadataCompat {
    return MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, data)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cover)
        .putString(YEAR, year)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, displayName)
        .putLong(LAST_MODIFIED, lastDateModified)
        .putLong(DURATION, duration)
        .putLong(IS_FAVOURITE, if (isFavourite) 1L else 0L)
//        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, cover)
//        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
//        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
//        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, artist)
        .build()
}

fun Audio.toMediaItem(): MediaBrowserCompat.MediaItem {
    val desc = MediaDescriptionCompat.Builder()
        .setMediaUri(data.toUri())
        .setTitle(title)
        .setDescription(artist)
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

fun MediaMetadataCompat.toAudio(): Audio {
    //        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, data)
    //        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
    //        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, cover)
    //        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, data)
    //        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cover)
    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, displayName)
    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, displayName)

    return Audio(
        data = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) ?: "",
        artist = getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: "",
        title = getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
        lastDateModified = getLong(LAST_MODIFIED),
        displayName = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION) ?: "",
        album = getString(MediaMetadataCompat.METADATA_KEY_ALBUM) ?: "",
        year = getString(YEAR) ?: "",
        cover = getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) ?: "",
        duration = getLong(MediaMetadataCompat.METADATA_KEY_DURATION),
        isFavourite = getLong(IS_FAVOURITE) == 1L
    )
}


