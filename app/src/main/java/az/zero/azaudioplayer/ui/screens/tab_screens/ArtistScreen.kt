package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.composables.ItemsHeader
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
            ItemsHeader(text = headerText)
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
    DBArtistWithAudios: DBArtistWithAudios,
    onClick: () -> Unit,
) {
    val artistName = DBArtistWithAudios.artist.name
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