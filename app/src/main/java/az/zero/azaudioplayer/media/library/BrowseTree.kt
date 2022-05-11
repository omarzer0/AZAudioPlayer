/*
 * Copyright 2019 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package az.zero.azaudioplayer.media.library

import android.content.Context
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.media.audio_data_source.AudioDataSource
import az.zero.azaudioplayer.media.extensions.*

/**
 * Represents a tree of media that's used by [MusicService.onLoadChildren].
 *
 * [BrowseTree] maps a media id (see: [MediaMetadataCompat.METADATA_KEY_MEDIA_ID]) to one (or
 * more) [MediaMetadataCompat] objects, which are children of that media id.
 *
 * For example, given the following conceptual tree:
 * root
 *  +-- Albums
 *  |    +-- Album_A
 *  |    |    +-- Song_1
 *  |    |    +-- Song_2
 *  ...
 *  +-- Artists
 *  ...
 *
 *  Requesting `browseTree["root"]` would return a list that included "Albums", "Artists", and
 *  any other direct children. Taking the media ID of "Albums" ("Albums" in this example),
 *  `browseTree["Albums"]` would return a single item list "Album_A", and, finally,
 *  `browseTree["Album_A"]` would return "Song_1" and "Song_2". Since those are leaf nodes,
 *  requesting `browseTree["Song_1"]` would return null (there aren't any children of it).
 */
class BrowseTree(context: Context, audioDataSource: AudioDataSource) {
    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    /**
     * In this example, there's a single root node (identified by the constant
     * [UAMP_BROWSABLE_ROOT]). The root's children are each album included in the
     * [MusicSource], and the children of each album are the songs on that album.
     * (See [BrowseTree.buildAlbumRoot] for more details.)
     *
     * TODO: Expand to allow more browsing types.
     */
    init {
        val rootList = mediaIdToChildren[BROWSABLE_ROOT] ?: mutableListOf()

        val recommendedMetadata = MediaMetadataCompat.Builder().apply {
            id = RECOMMENDED_ROOT
            title = "Recommended"
            albumArtUri = RESOURCE_ROOT_URI +
                    context.resources.getResourceEntryName(R.drawable.ic_image)
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        val albumsMetadata = MediaMetadataCompat.Builder().apply {
            id = ALBUMS_ROOT
            title = "Albums"
            albumArtUri = RESOURCE_ROOT_URI +
                    context.resources.getResourceEntryName(R.drawable.ic_image)
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        val fav = MediaMetadataCompat.Builder().apply {
            id = FAV_ROOT
            title = "Favourites"
            albumArtUri = RESOURCE_ROOT_URI +
                    context.resources.getResourceEntryName(R.drawable.ic_image)
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        rootList += recommendedMetadata
        rootList += albumsMetadata
        rootList += fav
        mediaIdToChildren[BROWSABLE_ROOT] = rootList

//        Log.e("MainViewModel", "audios size: ${audioDataSource.audios.size}")
//
//        audioDataSource.audios.forEach { mediaItem ->
////            val albumMediaId = ""
////            val albumChildren = mediaIdToChildren[albumMediaId] ?: buildAlbumRoot(mediaItem)
////            albumChildren += mediaItem
////
////            // Add the first track of each album to the 'Recommended' category
////            if (mediaItem.trackNumber == 1L) {
////                val recommendedChildren = mediaIdToChildren[RECOMMENDED_ROOT]
////                    ?: mutableListOf()
////                recommendedChildren += mediaItem
////                mediaIdToChildren[RECOMMENDED_ROOT] = recommendedChildren
////            }
//            val albumChildren = mediaIdToChildren[ALBUMS_ROOT] ?: buildAlbumRoot(mediaItem)
//            albumChildren += mediaItem
//            albumChildren += mediaItem
//        }
//        Log.e("MainViewModel", "Rec: ${mediaIdToChildren[RECOMMENDED_ROOT]?.size}")
//        Log.e("MainViewModel", "Alb: ${mediaIdToChildren[ALBUMS_ROOT]?.size}")
//        Log.e("MainViewModel", "FAV: ${mediaIdToChildren[FAV_ROOT]?.size}")

        audioDataSource.audiosLiveData.observeForever {
            Log.e("testForMLD", "$it")

            it.forEach { mediaItem ->
                //            val albumMediaId = ""
//            val albumChildren = mediaIdToChildren[albumMediaId] ?: buildAlbumRoot(mediaItem)
//            albumChildren += mediaItem
//
//            // Add the first track of each album to the 'Recommended' category
//            if (mediaItem.trackNumber == 1L) {
//                val recommendedChildren = mediaIdToChildren[RECOMMENDED_ROOT]
//                    ?: mutableListOf()
//                recommendedChildren += mediaItem
//                mediaIdToChildren[RECOMMENDED_ROOT] = recommendedChildren
//            }
                val albumChildren = mediaIdToChildren[ALBUMS_ROOT] ?: buildAlbumRoot(mediaItem)
                albumChildren += mediaItem
                albumChildren += mediaItem
            }
            Log.e("MainViewModel", "Rec: ${mediaIdToChildren[RECOMMENDED_ROOT]?.size}")
            Log.e("MainViewModel", "Alb: ${mediaIdToChildren[ALBUMS_ROOT]?.size}")
            Log.e("MainViewModel", "FAV: ${mediaIdToChildren[FAV_ROOT]?.size}")
        }



    }

    /**
     * Provide access to the list of children with the `get` operator.
     * i.e.: `browseTree\[UAMP_BROWSABLE_ROOT\]`
     */
    operator fun get(mediaId: String) = mediaIdToChildren[mediaId]

    /**
     * Builds a node, under the root, that represents an album, given
     * a [MediaMetadataCompat] object that's one of the songs on that album,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildAlbumRoot(mediaItem: MediaMetadataCompat): MutableList<MediaMetadataCompat> {
        val albumMetadata = MediaMetadataCompat.Builder().apply {
            id = mediaItem.album ?: "123"
            title = mediaItem.album
            artist = mediaItem.artist
            albumArt = mediaItem.albumArt
            albumArtUri = mediaItem.albumArtUri.toString()
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        // Adds this album to the 'Albums' category.
        val rootList = mediaIdToChildren[ALBUMS_ROOT] ?: mutableListOf()
        rootList += albumMetadata
        mediaIdToChildren[ALBUMS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[albumMetadata.id!!] = it
        }
    }
}

const val BROWSABLE_ROOT = "/"
const val EMPTY_ROOT = "@empty@"
const val RECOMMENDED_ROOT = "__RECOMMENDED__"
const val ALBUMS_ROOT = "__ALBUMS__"
const val FAV_ROOT = "__FAV__"

const val RESOURCE_ROOT_URI = "android.resource://com.example.android.uamp.next/drawable/"
