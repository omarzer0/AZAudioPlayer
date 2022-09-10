package az.zero.db.helpers

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import az.zero.base.di.ApplicationScope
import az.zero.db.AudioDao
import az.zero.db.entities.DBAlbum
import az.zero.db.entities.DBArtist
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioDbHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {

    fun compareWithLocalList(dao: AudioDao) {
        applicationScope.launch {
            val databaseList = dao.getAllDbAudioSingleList()
            val localList = getMusic()
            computeItemsToAdd(databaseList, localList, dao)
            computeItemsToDelete(databaseList, localList, dao)
            // TODO get all new audios and use them instead of localList
            computeAlbumItems(localList, dao)
            computeArtistItems(localList, dao)
            computeFavouritePlaylist(dao)
        }
    }

    private fun computeItemsToAdd(
        databaseList: List<DBAudio>,
        localList: List<DBAudio>,
        dao: AudioDao
    ) {
        applicationScope.launch {
            val listToAdd: MutableList<DBAudio> = mutableListOf()
            localList.forEach { newAudio ->
                val exist = databaseList.any { oldAudioList -> oldAudioList.data == newAudio.data }
                if (!exist) listToAdd.add(newAudio)
            }.also {
                listToAdd.forEach { audio -> dao.insert(audio) }
            }
        }
    }

    private fun computeFavouritePlaylist(dao: AudioDao) {
        applicationScope.launch {
            val favouriteList = dao.getAllDbAudioSingleList().filter { it.isFavourite }
            dao.deleteFavouritePlaylist()
            dao.addPlayList(
                // TODO y
                DBPlaylist(
                    name = context.getString(az.zero.base.R.string.favourites),
                    DBAudioList = favouriteList,
                    isFavouritePlaylist = true
                )
            )
        }
    }

    private fun computeItemsToDelete(
        databaseList: List<DBAudio>,
        localList: List<DBAudio>,
        dao: AudioDao
    ) {
        applicationScope.launch {
            val listToDelete: MutableList<DBAudio> = mutableListOf()
            databaseList.forEach { oldAudio ->
                val exist = localList.any { newAudioList -> newAudioList.data == oldAudio.data }
                if (!exist) listToDelete.add(oldAudio)
            }.also {
                listToDelete.forEach { audio -> dao.delete(audio) }
                removeDeletedAudioFromPlaylists(dao, listToDelete)
            }
        }
    }

    private fun removeDeletedAudioFromPlaylists(dao: AudioDao, listToDelete: List<DBAudio>) {
        val playlistsToAdd: MutableList<DBPlaylist> = mutableListOf()
        applicationScope.launch {
            val playlists = dao.getAllPlayListsWithoutFavouritePlaylist()
            playlists.forEach {
                val newAudioList = it.DBAudioList.filter { audio -> !listToDelete.contains(audio) }
                playlistsToAdd.add(DBPlaylist(it.name, newAudioList, it.isFavouritePlaylist))
            }
            dao.deleteAllPlaylistsWithoutFavourite()
            playlistsToAdd.forEach { dao.addPlayList(it) }
        }
    }

    private fun computeAlbumItems(
        localList: List<DBAudio>,
        dao: AudioDao
    ) {
        applicationScope.launch {
            val albums: MutableList<DBAlbum> = mutableListOf()
            localList.forEach { dbAudio ->
                val albumExist = albums.any { dbAlbum -> dbAlbum.name == dbAudio.album }
                if (!albumExist) albums.add(DBAlbum(dbAudio.album))
            }.also {
                dao.deleteAllAlbums()
                albums.forEach { album -> dao.insert(album) }
            }
        }
    }

    private fun computeArtistItems(
        localList: List<DBAudio>,
        dao: AudioDao
    ) {
        applicationScope.launch {
            val artists: MutableList<DBArtist> = mutableListOf()
            localList.forEach { dbAudio ->
                val artistExist = artists.any { dbArtist -> dbArtist.name == dbAudio.artist }
                if (!artistExist) artists.add(DBArtist(dbAudio.artist))
            }.also {
                dao.deleteAllArtists()
                artists.forEach { artist -> dao.insert(artist) }
                Log.e(
                    "computeArtistItems",
                    "computeArtistItems: \nlocal= ${localList.size}\nartists= ${artists.size}"
                )
            }
        }
    }


    @SuppressLint("Range")
    fun getMusic(): List<DBAudio> {
        val localList = mutableListOf<DBAudio>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " DESC"
        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
//                if (!File(data).exists()) continue

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
        // TODO y y = done
        return if (text == null || text == "<unknown>") context.getString(az.zero.base.R.string.unknown)
        else text
    }
}

