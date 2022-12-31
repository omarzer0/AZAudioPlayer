package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.composables.ui_extensions.mirror
import az.zero.db.entities.DBAudio

@Composable
fun BottomPlayer(
    modifier: Modifier = Modifier,
    currentlyPlayingAudio: DBAudio,
    isPlaying: Boolean,
    enabled: Boolean,
    onBodyClick: () -> Unit,
    onFavouriteClick: (Boolean) -> Unit,
    onPlayOrPauseClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickableSafeClick {
            if (enabled) onBodyClick()
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),
            color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray
        )

        Row(
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomImage(
                image = currentlyPlayingAudio.cover,
                modifier = Modifier.size(48.dp),
            )

            TopWithBottomText(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp, end = 24.dp),
                topTextString = currentlyPlayingAudio.title,
                bottomTextString = currentlyPlayingAudio.artist
            )

            IconButton(
                enabled = enabled,
                modifier = Modifier
                    .weight(0.1f)
                    .mirror(), onClick = { onFavouriteClick(currentlyPlayingAudio.isFavourite) }
            ) {
                Icon(
                    if (currentlyPlayingAudio.isFavourite) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    stringResource(id = R.string.add_or_remove_from_favourites),
                    tint = if (currentlyPlayingAudio.isFavourite) Color.Red else MaterialTheme.colors.onPrimary
                )
            }

            IconButton(
                enabled = enabled,
                modifier = Modifier
                    .weight(0.1f)
                    .mirror(),
                onClick = onPlayOrPauseClick
            ) {
                Icon(
                    if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    stringResource(id = R.string.play),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    }

}