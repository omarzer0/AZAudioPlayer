package az.zero.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
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


//fun DBAudio.toMediaMetadataCompat(): MediaMetadataCompat {
//    return MediaMetadataCompat.Builder()
//        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, data)
//        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
//        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
//        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
//        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cover)
//        .putString(YEAR, year)
//        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, displayName)
//        .putLong(LAST_MODIFIED, lastDateModified)
//        .putLong(DURATION, duration)
//        .putLong(IS_FAVOURITE, if (isFavourite) 1L else 0L)
////        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, cover)
////        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
////        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
////        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, artist)
//        .build()
//}
//
//fun DBAudio.toMediaItem(): MediaBrowserCompat.MediaItem {
////    val extras = Bundle().apply {
////        putInt(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toInt())
////    }
//    val desc = MediaDescriptionCompat.Builder()
//        .setMediaUri(data.toUri())
//        .setTitle(title)
//        .setDescription(artist)
//        .setSubtitle(displayName)
//        .setMediaId(data)
//        .setIconUri(cover.toUri())
////        .setExtras(extras)
//        .build()
//    return MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
//}
//
//fun DBAudio.toExoMediaItem(): MediaBrowser.MediaItem {
////    val extras = Bundle().apply {
////        putInt(MediaMetadataCompat.METADATA_KEY_DURATION, duration.toInt())
////    }
//    return MediaBrowser.MediaItem.Builder()
//        .setMediaId(data)
//        .setUri(data.toUri())
//        .setMediaMetadata(
//            MediaMetadata.Builder()
//                .setMediaUri(data.toUri())
//                .setTitle(title)
//                .setSubtitle(displayName)
//                .setArtist(artist)
//                .setAlbumTitle(album)
//                .setDescription(displayName)
////                .setExtras(extras)
//                .build()
//        ).build()
//}
//
//fun MediaMetadataCompat.toAudio(): DBAudio {
//    //        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, data)
//    //        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
//    //        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
//    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
//    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, cover)
//    //        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, data)
//    //        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, cover)
//    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, displayName)
//    //        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, displayName)
//
//    return DBAudio(
//        data = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) ?: "",
//        artist = getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: "",
//        title = getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "",
//        lastDateModified = getLong(LAST_MODIFIED),
//        displayName = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION) ?: "",
//        album = getString(MediaMetadataCompat.METADATA_KEY_ALBUM) ?: "",
//        year = getString(YEAR) ?: "",
//        cover = getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI) ?: "",
//        duration = getLong(MediaMetadataCompat.METADATA_KEY_DURATION),
//        isFavourite = getLong(IS_FAVOURITE) == 1L
//    )
//}
//

