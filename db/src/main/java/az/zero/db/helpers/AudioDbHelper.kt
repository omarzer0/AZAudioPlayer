package az.zero.db.helpers

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import az.zero.base.constants.FAVOURITE_PLAY_LIST_ID
import az.zero.base.di.ApplicationScope
import az.zero.db.AudioDao
import az.zero.db.entities.DBAlbum
import az.zero.db.entities.DBArtist
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject

class AudioDbHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val dao: AudioDao,
) {
    private var scanJob :Job? = null

    fun searchForNewAudios(
        skipRecordings: Boolean = true,
        skipAndroidFiles: Boolean = true,
        onFinish: (suspend () -> Unit)? = null,
    ) {
        scanJob?.cancel()
        scanJob = applicationScope.launch {
            val databaseList = dao.getAllDbAudioSingleList()
            val localList = getMusic(
                skipRecordings = skipRecordings,
                skipAndroidFiles = skipAndroidFiles
            )

            val added = async { computeItemsToAdd(databaseList, localList) }
            // TODO get all new audios and use them instead of localList
            val deleted = async { computeItemsToDelete(databaseList, localList) }
            launch { computeAlbumItems(localList) }
            launch { computeArtistItems(localList) }
            launch { computeFavouritePlaylist() }

            awaitAll(added, deleted)
            onFinish?.invoke()
        }
    }

    private suspend fun computeItemsToAdd(
        databaseList: List<DBAudio>,
        localList: List<DBAudio>,
    ) {
        val listToAdd: MutableList<DBAudio> = mutableListOf()
        localList.forEach { newAudio ->
            val exist = databaseList.any { oldAudioList -> oldAudioList.data == newAudio.data }
            if (!exist) listToAdd.add(newAudio)
        }.also {
            listToAdd.forEach { audio -> dao.insert(audio) }
        }
    }

    private suspend fun computeFavouritePlaylist() {
        val favouriteList = dao.getAllDbAudioSingleList().filter { it.isFavourite }
        dao.deleteFavouritePlaylist()
        dao.addPlayList(
            DBPlaylist(
                id = FAVOURITE_PLAY_LIST_ID,
                name = context.getString(az.zero.base.R.string.favourites),
                dbAudioList = favouriteList,
                isFavouritePlaylist = true
            )
        )
    }

    private suspend fun computeItemsToDelete(
        databaseList: List<DBAudio>,
        localList: List<DBAudio>,
    ) {
        val listToDelete: MutableList<DBAudio> = mutableListOf()
        databaseList.forEach { oldAudio ->
            val exist = localList.any { newAudioList -> newAudioList.data == oldAudio.data }
            if (!exist) listToDelete.add(oldAudio)
        }.also {
            listToDelete.forEach { audio -> dao.delete(audio) }
            removeDeletedAudioFromPlaylists(listToDelete)
        }
    }

    private suspend fun removeDeletedAudioFromPlaylists(listToDelete: List<DBAudio>) {
        val playlistsToAdd: MutableList<DBPlaylist> = mutableListOf()
        val playlists = dao.getAllPlayListsWithoutFavouritePlaylistOnce()
        playlists.forEach {
            val newAudioList = it.dbAudioList.filter { audio -> !listToDelete.contains(audio) }
            playlistsToAdd.add(DBPlaylist(it.name, newAudioList, it.isFavouritePlaylist))
        }
        dao.deleteAllPlaylistsWithoutFavourite()
        playlistsToAdd.forEach { dao.addPlayList(it) }
    }

    private suspend fun computeAlbumItems(
        localList: List<DBAudio>,
    ) {
        val albums: MutableList<DBAlbum> = mutableListOf()
        localList.forEach { dbAudio ->
            val albumExist = albums.any { dbAlbum -> dbAlbum.name == dbAudio.album }
            if (!albumExist) albums.add(DBAlbum(dbAudio.album))
        }.also {
            dao.deleteAllAlbums()
            albums.forEach { album -> dao.insert(album) }
        }
    }

    private suspend fun computeArtistItems(
        localList: List<DBAudio>,
    ) {
        val artists: MutableList<DBArtist> = mutableListOf()
        localList.forEach { dbAudio ->
            val artistExist = artists.any { dbArtist -> dbArtist.name == dbAudio.artist }
            if (!artistExist) artists.add(DBArtist(dbAudio.artist))
        }.also {
            dao.deleteAllArtists()
            artists.forEach { artist -> dao.insert(artist) }
        }
    }

    @SuppressLint("Range")
    private fun getMusic(
        skipRecordings: Boolean = true,
        skipAndroidFiles: Boolean = true,
    ): List<DBAudio> {
        val localList = mutableListOf<DBAudio>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " DESC"
        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                if (skipRecordings) {
                    if (data.lowercase().contains("Recording".lowercase())) continue
                }

                if (skipAndroidFiles) {
                    if (data.lowercase().contains("Android/".lowercase())) continue
                }

//                if (data.lowercase().contains("Recording".lowercase()) || data.lowercase()
//                        .contains("Android/".lowercase())
//                ) continue

                val title =
                    getOrUnknown(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)))

                val artist =
                    getOrUnknown(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))

                val lastDateModified =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))

                val displayName =
                    getOrUnknown(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)))

                val album =
                    getOrUnknown(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)))

                val year =
                    getOrUnknown(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR)))

                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                val sArtworkUri = Uri.parse("content://media/external/audio/albumart")

                val albumArtUri = ContentUris.withAppendedId(sArtworkUri, id)

                val cover = albumArtUri.toString()

                val duration =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))

                val dbAudio = DBAudio(
                    data, title, artist, lastDateModified, displayName, album, year, cover, duration
                )

                localList.add(dbAudio)
            }
        }
        cursor?.close()
        return localList
    }

    private fun getOrUnknown(text: String?): String {
        return if (text == null || text == "<unknown>") context.getString(az.zero.base.R.string.unknown)
        else text
    }
}

