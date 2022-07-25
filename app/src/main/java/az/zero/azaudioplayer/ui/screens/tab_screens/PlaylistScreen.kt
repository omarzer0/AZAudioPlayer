package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Playlist
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.composables.LocalImageIcon
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick

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

        item {
            AddPlayList {

            }
        }
    }
}

@Composable
fun AddPlayList(
    modifier: Modifier = Modifier,
    onAddPlayListClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick { onAddPlayListClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocalImageIcon(
            localImageUrl = Icons.Filled.Add,
            addBorder = false,
            iconTint = SecondaryTextColor,
            imageBackgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier.border(width = 1.dp, SecondaryTextColor, RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(id = R.string.new_playlist),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .weight(0.6f),
        )
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