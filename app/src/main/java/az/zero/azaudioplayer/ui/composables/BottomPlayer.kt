package az.zero.azaudioplayer.ui.composables

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.media.player.extensions.isPlaying
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.utils.clickableSafeClick
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror
import az.zero.player.EMPTY_AUDIO

@Composable
fun BottomPlayer(modifier: Modifier = Modifier, viewModel: HomeViewModel, onBodyClick: () -> Unit) {
    val currentPlayingAudio = viewModel.currentPlayingAudio.observeAsState()
    val audio = currentPlayingAudio.value ?: EMPTY_AUDIO
    val playingState = viewModel.playbackState.observeAsState()
    val isPlaying = playingState.value?.isPlaying ?: false
    val enabled = audio.data.isNotEmpty()

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
            color = MaterialTheme.colors.background
        )

        Row(
            modifier = Modifier.padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomImage(image = audio.cover)

            TopWithBottomText(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(start = 16.dp, end = 24.dp),
                topTextName = audio.title,
                bottomTextName = audio.artist
            )

            IconButton(
                enabled = enabled,
                modifier = Modifier
                    .weight(0.1f)
                    .mirror(), onClick = { viewModel.addOrRemoveFromFavourite(audio) }
            ) {
                Icon(
                    if (audio.isFavourite) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    stringResource(id = R.string.add_or_remove_from_favourites),
                    tint = if (audio.isFavourite) Color.Red else MaterialTheme.colors.onPrimary
                )
            }

            IconButton(
                enabled = enabled,
                modifier = Modifier
                    .weight(0.1f)
                    .mirror(), onClick = {
                    viewModel.playOrPause()
                }
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