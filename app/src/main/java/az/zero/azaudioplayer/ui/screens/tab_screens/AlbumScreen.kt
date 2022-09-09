package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Album
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor


@Composable
fun AlbumScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val albumList = viewModel.allAlbums.observeAsState().value
    if (albumList.isNullOrEmpty()) return

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "${albumList.size} ${stringResource(id = R.string.of_albums)}"
            ItemsHeader(text = headerText)
        }

        items(items = albumList, key = { it.album.name }) { album ->
            AlbumItem(album = album) {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToAlbumDetailsFragment(
                        album.audioList.toTypedArray()
                    )
                )
            }
        }

    }
}

@Composable
fun AlbumItem(album: Album, onClick: () -> Unit) {

    val image = remember {
        if (album.audioList.isNullOrEmpty()) ""
        else album.audioList[0].cover
    }

    BasicAudioItem(
        imageUrl = image,
        topText = album.album.name,
        bottomText = album.audioList[0].artist,
        topTextColor = MaterialTheme.colors.onPrimary,
        iconVector = Icons.Filled.KeyboardArrowRight,
        iconColor = SecondaryTextColor,
        iconText = stringResource(id = R.string.more),
        onItemClick = onClick
    )
}