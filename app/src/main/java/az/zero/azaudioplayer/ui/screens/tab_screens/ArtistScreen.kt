package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Artist
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
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
    onTailItemClick: (() -> Unit)? = null
) {
    val artistName = artist.artist.name
    val textColor = MaterialTheme.colors.onPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp)
    ) {
        Text(
            text = artistName,
            color = textColor,
            style = MaterialTheme.typography.h2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .align(CenterVertically)
                .weight(0.6f),

            )
        Spacer(modifier = Modifier.width(16.dp))

        if (onTailItemClick != null) {
            IconButton(
                modifier = Modifier
                    .weight(0.1f)
                    .align(CenterVertically)
                    .mirror(), onClick = {}) {
                Icon(
                    Icons.Filled.KeyboardArrowRight,
                    stringResource(id = R.string.more),
                    tint = SecondaryTextColor
                )
            }
        } else {
            Icon(
                Icons.Filled.KeyboardArrowRight,
                stringResource(id = R.string.more),
                tint = SecondaryTextColor,
                modifier = Modifier
                    .weight(0.1f)
                    .align(CenterVertically)
                    .mirror(),
            )
        }

    }
}