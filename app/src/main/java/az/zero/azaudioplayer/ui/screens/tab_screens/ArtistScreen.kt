package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Artist
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.utils.LONG_TEXT
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror


@Composable
fun ArtistScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {
    val artistList = remember { viewModel.allArtists }.observeAsState().value
    if (artistList.isNullOrEmpty()) return

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "${artistList.size} ${stringResource(id = R.string.of_artists)}"
            ItemsHeader(text = headerText)
        }

        items(artistList, key = { it.artist.name }) { artist ->
            ArtistItem(artist, onClick = {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToAlbumDetailsFragment(
                        artist.audioList.toTypedArray()
                    )
                )
            })
        }

    }
}

@Composable
fun ArtistItem(
    artist: Artist,
    onClick: () -> Unit,
) {
    val artistName = artist.artist.name
    val textColor = MaterialTheme.colors.onPrimary

    BasicAudioItem(
        cornerShape = CircleShape,
        topText = artistName,
        topTextColor = textColor,
        iconVector = Icons.Filled.KeyboardArrowRight,
        iconColor = SecondaryTextColor,
        iconText = stringResource(id = R.string.more),
        onItemClick = onClick,
    )

}