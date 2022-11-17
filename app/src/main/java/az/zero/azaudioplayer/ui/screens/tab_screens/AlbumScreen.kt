package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.PlayAllHeader
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.composables.clickableSafeClick
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.composables.ui_extensions.mirror
import az.zero.db.entities.DBAlbumWithAudioList


@Composable
fun AlbumScreen(
    viewModel: HomeViewModel,
    navController: NavController,
) {
    val albumList = viewModel.allAlbums.observeAsState().value ?: emptyList()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "${albumList.size} ${stringResource(id = R.string.of_albums)}"
            PlayAllHeader(text = headerText)
        }

        items(items = albumList, key = { it.album.name }) { album ->
            AlbumItem(dbAlbumWithAudioList = album) {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToAlbumDetailsFragment(
                        album
                    )
                )
            }
        }

    }
}

@Composable
fun AlbumItem(dbAlbumWithAudioList: DBAlbumWithAudioList, onClick: () -> Unit) {

    val image = if (dbAlbumWithAudioList.dbAudioList.isEmpty()) ""
    else dbAlbumWithAudioList.dbAudioList[0].cover

    val artistName = if (dbAlbumWithAudioList.dbAudioList.isEmpty()) ""
    else dbAlbumWithAudioList.dbAudioList[0].artist

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomImage(
            modifier = Modifier.size(48.dp),
            image = image
        )

        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomText(
            modifier = Modifier.weight(1f),
            topTextString = dbAlbumWithAudioList.album.name,
            bottomTextString = artistName,
            topTextStyle = MaterialTheme.typography.h2,
            bottomTextStyle = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = SecondaryTextColor,
            modifier = Modifier.mirror()
        )
    }
}