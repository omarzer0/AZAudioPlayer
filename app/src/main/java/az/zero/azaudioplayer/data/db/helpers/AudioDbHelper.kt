package az.zero.azaudioplayer.data.db.helpers

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.data.db.AudioDao
import az.zero.azaudioplayer.di.ApplicationScope
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.domain.models.DBAlbum
import az.zero.azaudioplayer.domain.models.DBArtist
import az.zero.azaudioplayer.domain.models.Playlist
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
            computeAlbumItems(localList, dao)
            computeArtistItems(localList, dao)
            computeFavouritePlaylist(dao, localList)
        }
    }

    private fun computeItemsToAdd(
        databaseList: List<Audio>,
        localList: List<Audio>,
        dao: AudioDao
    ) {
        applicationScope.launch {
            val listToAdd: MutableList<Audio> = mutableListOf()
            localList.forEach { newAudio ->
                val exist = databaseList.any { oldAudioList -> oldAudioList.data == newAudio.data }
                if (!exist) listToAdd.add(newAudio)
            }.also {
                listToAdd.forEach { audio -> dao.insert(audio) }
            }
        }
    }

    private fun computeFavouritePlaylist(dao: AudioDao, audioList: List<Audio>) {
        audioList.filter { it.isFavourite }.also {
            applicationScope.launch {
                dao.deleteFavouritePlaylist()
                dao.addPlayList(
                    Playlist(
                        name = context.getString(R.string.favourites),
                        audioList = it,
                        isFavouritePlaylist = true
                    )
                )
            }
        }
    }

    private fun computeItemsToDelete(
        databaseList: List<Audio>,
        localList: List<Audio>,
        dao: AudioDao
    ) {
        applicationScope.launch {
            val listToDelete: MutableList<Audio> = mutableListOf()
            databaseList.forEach { oldAudio ->
                val exist = localList.any { newAudioList -> newAudioList.data == oldAudio.data }
                if (!exist) listToDelete.add(oldAudio)
            }.also {
                listToDelete.forEach { audio -> dao.delete(audio) }
                removeDeletedAudioFromPlaylists(dao, listToDelete)
            }
        }
    }

    private fun removeDeletedAudioFromPlaylists(dao: AudioDao, listToDelete: List<Audio>) {
        val playlistsToAdd: MutableList<Playlist> = mutableListOf()
        applicationScope.launch {
            val playlists = dao.getAllPlayListsWithoutFavouritePlaylist()
            playlists.forEach {
                val newAudioList = it.audioList.filter { audio -> !listToDelete.contains(audio) }
                playlistsToAdd.add(Playlist(it.name, newAudioList, it.isFavouritePlaylist))
            }
            dao.deleteAllPlaylistsWithoutFavourite()
            playlistsToAdd.forEach { dao.addPlayList(it) }
        }
    }

    private fun computeAlbumItems(
        localList: List<Audio>,
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
        localList: List<Audio>,
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
            }
        }
    }


    @SuppressLint("Range")
    fun getMusic(): List<Audio> {
        val localList = mutableListOf<Audio>()
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

                val dbAudio = Audio(
                    data, title, artist, lastDateModified, displayName, album, year, cover, duration
                )

                localList.add(dbAudio)
            }
        }
        cursor?.close()
        return localList
    }

    private fun getOrUnknown(text: String?): String {
        return if (text == null || text == "<unknown>") context.getString(R.string.unknown)
        else text
    }
}

