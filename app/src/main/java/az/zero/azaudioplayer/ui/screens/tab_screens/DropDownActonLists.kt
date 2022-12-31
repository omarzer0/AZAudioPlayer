package az.zero.azaudioplayer.ui.screens.tab_screens

import android.content.Context
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.screens.home.HomeDropdownActions

fun getAudioActionsList(context: Context) = listOf(
    Pair(
        context.getString(R.string.search_local_audio),
        HomeDropdownActions.SearchLocalAudio
    ),
    Pair(
        context.getString(R.string.sort_audios_by),
        HomeDropdownActions.SortAudiosBy
    ),
    Pair(context.getString(R.string.settings), HomeDropdownActions.Settings),

    // TODO* to be add later when manage audios
//    Pair(
//        context.getString(R.string.manage_audios),
//        HomeDropdownActions.ManageAudios
//    )
)

fun getArtistActionsList(context: Context) = listOf(
    Pair(
        context.getString(R.string.search_local_audio),
        HomeDropdownActions.SearchLocalAudio
    ),
    // TODO* to be add later when manage audios
//    Pair(
//        context.getString(R.string.manage_artists),
//        HomeDropdownActions.ManageArtists
//    ),
    Pair(context.getString(R.string.settings), HomeDropdownActions.Settings)
)

fun getAlbumsActionsList(context: Context) = listOf(
    Pair(
        context.getString(R.string.search_local_audio),
        HomeDropdownActions.SearchLocalAudio
    ),

    Pair(
        context.getString(R.string.sort_albums_by),
        HomeDropdownActions.SortAlbumsBy

    ),

    // TODO* to be add later when manage audios
//    Pair(
//        context.getString(R.string.manage_albums),
//        HomeDropdownActions.ManageAlbums
//    ),

    Pair(context.getString(R.string.settings), HomeDropdownActions.Settings)
)

fun getPlaylistActionsList(context: Context) = listOf(
    Pair(
        context.getString(R.string.search_local_audio),
        HomeDropdownActions.SearchLocalAudio
    ),
    Pair(
        context.getString(R.string.manage_playlists),
        HomeDropdownActions.ManagePlaylists
    ),
    Pair(
        context.getString(R.string.settings),
        HomeDropdownActions.Settings
    )
)