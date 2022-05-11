package az.zero.azaudioplayer.db.audio_helper

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.db.entities.DBAlbum
import az.zero.azaudioplayer.db.entities.DBAudio
import az.zero.azaudioplayer.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioDbHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val applicationScope: CoroutineScope,
) {

    fun compare(
        oldList: List<DBAudio>, // from db
        newList: List<DBAudio>, // from local files
        listToDeleteFromDb: suspend (List<DBAudio>) -> Unit,
        listToAddToDb: suspend (List<DBAudio>) -> Unit,
        listOfAlbums: suspend (List<DBAlbum>) -> Unit
    ) {
        val listToDelete: MutableList<DBAudio> = mutableListOf()
        oldList.forEach { oldAudio ->
            val exist = newList.any { newAudioList -> newAudioList.data == oldAudio.data }
            if (!exist) listToDelete.add(oldAudio)
        }.also { applicationScope.launch { listToDeleteFromDb(listToDelete) } }

        val listToAdd: MutableList<DBAudio> = mutableListOf()
        newList.forEach { newAudio ->
            val exist = oldList.any { oldAudioList -> oldAudioList.data == newAudio.data }
            if (!exist) listToAdd.add(newAudio)
        }.also { applicationScope.launch { listToAddToDb(listToAdd) } }

        val albums: MutableList<DBAlbum> = mutableListOf()
        newList.forEach { dbAudio ->
            val albumExist = albums.any { dbAlbum ->
                dbAlbum.name == dbAudio.album
            }

            if (!albumExist) albums.add(DBAlbum(dbAudio.album))
        }.also { applicationScope.launch { listOfAlbums(albums) } }
    }

    fun compareWithLocalList(
        databaseList: List<DBAudio>,
        listToDeleteFromDb: suspend (List<DBAudio>) -> Unit,
        listToAddToDb: suspend (List<DBAudio>) -> Unit,
        listOfAlbums: suspend (List<DBAlbum>) -> Unit
    ) {
        Log.e("getAllDbAudio", "compareWithLocalList")

        val newList = getMusic()
        Log.e("getAllDbAudio", "compareWithLocalList $newList")

        compare(databaseList, newList, listToDeleteFromDb, listToAddToDb,listOfAlbums)
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
                    ?: continue
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    ?: context.getString(R.string.unknown)
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    ?: context.getString(R.string.unknown)
                val lastDateModified =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_MODIFIED))
                val displayName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                        ?: context.getString(R.string.unknown)
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    ?: context.getString(R.string.unknown)
                val year = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.YEAR))
                    ?: context.getString(R.string.unknown)
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                val sArtworkUri = Uri.parse("content://media/external/audio/albumart")
                val albumArtUri = ContentUris.withAppendedId(sArtworkUri, id)
                val cover = albumArtUri.toString()

                val dbAudio = DBAudio(
                    data, title, artist, lastDateModified, displayName, album, year, cover
                )
                localList.add(dbAudio)
            }
        }
        cursor?.close()
        return localList
    }
}

