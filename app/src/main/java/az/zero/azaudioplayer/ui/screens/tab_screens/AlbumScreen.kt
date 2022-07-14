package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Album
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror


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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
    ) {

        val image = remember {
            if (album.audioList.isNullOrEmpty()) ""
            else album.audioList[0].cover
        }

        CustomImage(image = image)

        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomText(
            modifier = Modifier.weight(0.6f),
            album.album.name,
            MaterialTheme.colors.onPrimary,
            album.audioList[0].artist
        )

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            modifier = Modifier
                .weight(0.1f)
                .mirror(), onClick = {}) {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                stringResource(id = R.string.more),
                tint = SecondaryTextColor
            )
        }
    }
}