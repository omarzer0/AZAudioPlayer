package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import az.zero.azaudioplayer.ui.composables.TextHeader
import az.zero.azaudioplayer.ui.composables.clickableSafeClick
import az.zero.azaudioplayer.ui.composables.ui_extensions.mirror
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.db.entities.DBArtistWithAudios

/**
 *  Stateful version of the ArtistScreen
 * */
@Composable
fun ArtistScreen(
    viewModel: HomeViewModel,
    navController: NavController,
) {
    val allArtists = viewModel.allArtists.observeAsState().value ?: emptyList()
    ArtistScreen(artistList = allArtists) { artist ->
        navController.navigate(
            HomeFragmentDirections.actionHomeFragmentToArtistDetailsFragment(
                artist
            )
        )
    }

}

/**
 *  Stateless version of the ArtistScreen
 * */
@Composable
private fun ArtistScreen(
    artistList: List<DBArtistWithAudios>,
    onArtistClick: (DBArtistWithAudios) -> Unit,
) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "${artistList.size} ${stringResource(id = R.string.of_artists)}"
            TextHeader(text = headerText)
        }

        items(artistList, key = { it.artist.name }) { artist ->
            ArtistItem(artist, onClick = {
                onArtistClick(artist)
            })
        }

    }
}

@Composable
fun ArtistItem(
    dbArtistWithAudios: DBArtistWithAudios,
    onClick: () -> Unit,
) {
    val artistName = dbArtistWithAudios.artist.name
    val textColor = MaterialTheme.colors.onPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = artistName,
            style = MaterialTheme.typography.h2.copy(color = textColor)
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