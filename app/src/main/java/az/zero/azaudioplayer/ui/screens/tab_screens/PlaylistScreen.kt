package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Playlist
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor

@Composable
fun PlaylistScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {

    val allPlaylist = viewModel.allPlaylists.observeAsState().value
    if (allPlaylist.isNullOrEmpty()) return

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        items(items = allPlaylist, key = { it.name }) { playlist ->
            PlaylistItem(playlist = playlist) {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToAlbumDetailsFragment(
                        playlist.audioList.toTypedArray()
                    )
                )
            }
        }

    }
}

@Composable
fun PlaylistItem(playlist: Playlist, onClick: () -> Unit) {
    val image = if (playlist.isFavouritePlaylist) R.drawable.ic_fav else R.drawable.ic_music
    val backgroundColor = if (playlist.isFavouritePlaylist) Color.Red else Color.White

    BasicAudioItem(
        imageUrl = null,
        localImageUrl = image,
        onItemClick = { onClick() },
        topText = playlist.name,
        bottomText = "${playlist.audioList.size} ${stringResource(id = R.string.audios)}",
        iconVector = Icons.Filled.KeyboardArrowRight,
        iconColor = SecondaryTextColor,
        iconText = stringResource(id = R.string.more),
        imageBackgroundColor = backgroundColor,
        addBorder = !playlist.isFavouritePlaylist
    )
}