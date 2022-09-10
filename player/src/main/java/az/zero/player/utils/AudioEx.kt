package az.zero.player.utils

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBAudio.Companion.DURATION
import az.zero.db.entities.DBAudio.Companion.IS_FAVOURITE
import az.zero.db.entities.DBAudio.Companion.LAST_MODIFIED
import az.zero.db.entities.DBAudio.Companion.YEAR
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata


fun DBAudio.toMediaMetadataCompat(): MediaMetadataCompat {
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

fun DBAudio.toMediaItem(): MediaBrowserCompat.MediaItem {
//    val extras = Bundle().apply {
//        putInt(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toInt())
//    }
    val desc = MediaDescriptionCompat.Builder()
        .setMediaUri(data.toUri())
        .setTitle(title)
        .setDescription(artist)
        .setSubtitle(displayName)
        .setMediaId(data)
        .setIconUri(cover.toUri())
//        .setExtras(extras)
        .build()
    return MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
}

fun DBAudio.toExoMediaItem(): MediaItem {
//    val extras = Bundle().apply {
//        putInt(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toInt())
//    }
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
//                .setExtras(extras)
                .build()
        ).build()
}

fun MediaMetadataCompat.toAudio(): DBAudio {
    //        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, data)
    //        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
    //        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, cover)
    //        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, data)
    //        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cover)
    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, displayName)
    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, displayName)

    return DBAudio(
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