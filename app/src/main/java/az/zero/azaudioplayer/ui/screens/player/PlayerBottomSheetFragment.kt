package az.zero.azaudioplayer.ui.screens.player

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.REPEAT_MODE_ONE
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.setCompContent
import az.zero.azaudioplayer.media.player.extensions.EMPTY_AUDIO
import az.zero.player.extensions.isPlaying
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.utils.clickableSafeClick
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror
import az.zero.azaudioplayer.utils.createTimeLabel
import az.zero.azaudioplayer.utils.largeIconSize
import az.zero.azaudioplayer.utils.midIconsSize
import az.zero.azaudioplayer.utils.smallIconsSize
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: PlayerBottomSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val modalBottomSheetBehavior = (this.dialog as BottomSheetDialog).behavior
        modalBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        modalBottomSheetBehavior.skipCollapsed = true

        return setCompContent(requireContext()) {
            PlayerScreen(viewModel = viewModel, navController = findNavController())
        }
    }
}


@Composable
fun PlayerScreen(
    viewModel: PlayerBottomSheetViewModel,
    navController: NavController
) {

    val currentPlayingAudio = viewModel.currentPlayingAudio.observeAsState()
    val playingState = viewModel.playbackState.observeAsState()
    val isPlaying = playingState.value?.isPlaying ?: false
    val audio = currentPlayingAudio.value ?: EMPTY_AUDIO
    val currentPosition = viewModel.currentPosition.observeAsState()
    val repeatMode =
        viewModel.repeatMode.observeAsState().value ?: PlaybackStateCompat.REPEAT_MODE_ALL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .verticalScroll(rememberScrollState())
    ) {

        TopBar {
            navController.navigateUp()
        }

        TopWithBottomText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            topTextName = audio.title,
            topTextColor = MaterialTheme.colors.onPrimary,
            bottomTextName = audio.artist,
            bottomTextColor = SecondaryTextColor,
            topTextStyle = MaterialTheme.typography.h1
        )

        CustomImage(
            image = audio.cover,
            modifier = Modifier
                .align(CenterHorizontally)
                .width(300.dp)
                .height(250.dp)
        )

        // TODO need cleaning up!
        var stopAutoUpdate by remember { mutableStateOf(false) }
        var autoUpdateValue by remember(currentPosition.value) {
            mutableStateOf(currentPosition.value ?: 0)
        }
        var userDraggedValue by remember { mutableStateOf(0f) }

        AudioSeekbar(
            sliderValue = when {
                stopAutoUpdate -> userDraggedValue
                else -> {
                    autoUpdateValue.toFloat()
                }
            },
            totalTime = audio.duration.toFloat(),
            onValueChanged = {
                stopAutoUpdate = true
                userDraggedValue = it
                autoUpdateValue = it.toLong()
            },
            onValueChangeFinished = { userDragPosition ->
                viewModel.seekToPosition(userDragPosition.toLong())
                stopAutoUpdate = false
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomActionsRow(
            isFavourite = audio.isFavourite,
            repeatMode = repeatMode,
            onFavouriteClick = { viewModel.addOrRemoveFromFavourite(audio) },
            onChangeRepeatModeClick = { viewModel.changeRepeatMode() }
        )

        AudioActionsRow(
            isPlaying = isPlaying,
            onPreviousClick = { viewModel.playPrevious() },
            onPlayPauseClick = { viewModel.playOrPause() },
            onNextClick = { viewModel.playNext() }
        )
    }
}

@Composable
fun TopBar(onDragDownClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Center,
    ) {

        IconButton(
            modifier = Modifier
                .mirror()
                .align(CenterStart),
            onClick = {

            }
        ) {
            Icon(
                Icons.Filled.MoreVert,
                stringResource(id = R.string.more),
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickableSafeClick { onDragDownClick() },
            contentAlignment = Center
        ) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                stringResource(id = R.string.drag),
                tint = Color.Gray,
                modifier = Modifier.size(midIconsSize)
            )
        }
    }
}

@Composable
fun AudioSeekbar(
    sliderValue: Float,
    totalTime: Float,
    onValueChanged: (Float) -> Unit,
    onValueChangeFinished: (userDragPosition: Float) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                onValueChanged(newValue)
            },
            valueRange = 0f..totalTime,
            onValueChangeFinished = {
                onValueChangeFinished(sliderValue)
            },
            colors = SliderDefaults.colors(
                thumbColor = SelectedColor,
                activeTrackColor = SelectedColor,
                inactiveTrackColor = MaterialTheme.colors.background
            ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = createTimeLabel(sliderValue),
                style = MaterialTheme.typography.body2,
                color = SecondaryTextColor
            )

            Text(
                text = createTimeLabel(totalTime),
                style = MaterialTheme.typography.body2,
                color = SecondaryTextColor
            )
        }

    }
}

@Composable
fun CustomActionsRow(
    modifier: Modifier = Modifier,
    isFavourite: Boolean,
    repeatMode: Int,
    onFavouriteClick: () -> Unit,
    onChangeRepeatModeClick: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val context = LocalContext.current
        var toast: Toast? by remember { mutableStateOf(null) }
        IconButton(
            onClick = {
                onChangeRepeatModeClick()
                val message = when (repeatMode) {
                    // reversed
                    REPEAT_MODE_ONE -> "Repeat All"
                    else -> "Repeat Once"
                }
                toast?.cancel()
                toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
                toast?.show()
            }
        ) {

            Icon(
                imageVector = when (repeatMode) {
                    REPEAT_MODE_ONE -> Icons.Filled.RepeatOne
                    else -> Icons.Filled.Repeat
                },
                stringResource(id = R.string.more),
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(smallIconsSize)
            )
        }

        IconButton(
            modifier = Modifier.mirror(),
            onClick = {
                onFavouriteClick()
            }
        ) {

            Icon(
                imageVector = if (isFavourite) Icons.Filled.Favorite
                else Icons.Outlined.FavoriteBorder,
                stringResource(id = R.string.add_or_remove_from_favourites),
                tint = if (isFavourite) SelectedColor
                else MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(smallIconsSize)
            )
        }

        IconButton(
            modifier = Modifier.mirror(),
            onClick = {}
        ) {

            Icon(
                imageVector = Icons.Filled.ListAlt,
                stringResource(id = R.string.more),
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(smallIconsSize)
            )
        }

    }
}


@Composable
fun AudioActionsRow(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onPreviousClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            modifier = Modifier.mirror(),
            onClick = { onPreviousClick() }
        ) {

            Icon(
                imageVector = Icons.Filled.SkipPrevious,
                stringResource(id = R.string.previous),
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(midIconsSize)
            )
        }

        IconButton(
            modifier = Modifier.mirror(),
            onClick = {
                Log.e("AudioActionsRowBefore", "AudioActionsRow: $isPlaying")
                onPlayPauseClick()
                Log.e("AudioActionsRowBefore", "AudioActionsRow: $isPlaying")

            }
        ) {

            Icon(
                imageVector = if (isPlaying) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                if (isPlaying) stringResource(id = R.string.pause) else stringResource(id = R.string.play),
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(largeIconSize)
            )
        }

        IconButton(
            modifier = Modifier.mirror(),
            onClick = { onNextClick() }
        ) {

            Icon(
                imageVector = Icons.Filled.SkipNext,
                stringResource(id = R.string.next),
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(midIconsSize)
            )
        }

    }
}

