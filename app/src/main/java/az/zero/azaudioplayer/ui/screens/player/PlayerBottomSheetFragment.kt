package az.zero.azaudioplayer.ui.screens.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.media.player.EMPTY_AUDIO
import az.zero.azaudioplayer.media.player.extensions.isPlaying
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.utils.*
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror
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
    var position = currentPosition.value ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .verticalScroll(rememberScrollState())
    ) {

        TopBar()

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

        AudioSeekbar(position,
            totalTime = audio.duration,
            isPlaying = isPlaying,
            viewModel = viewModel,
            onValueChange = {
                position = it
            },
            onValueChangeFinished = {
                viewModel.seekToPosition(it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        var isFav by remember { mutableStateOf(true) }

        CustomActionsRow(
            isFavourite = isFav,
            onFavouriteClick = { isFav = !isFav }
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
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        IconButton(
            modifier = Modifier.mirror(),
            onClick = {}
        ) {
            Icon(
                Icons.Filled.MoreVert,
                stringResource(id = R.string.more),
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .padding(end = 16.dp)
                .background(Red)
        )
    }
}

@Composable
fun AudioSeekbar(
    sliderPosition: Long,
    totalTime: Long,
    isPlaying: Boolean,
    viewModel: PlayerBottomSheetViewModel,
    onValueChange: (Long) -> Unit,
    onValueChangeFinished: (Long) -> Unit
) {
    var sliderValue by remember(key1 = sliderPosition) { mutableStateOf(sliderPosition) }
    var manuallyStopped by remember(key1 = true) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        val interactionSource = remember { MutableInteractionSource() }
        val isDragged by interactionSource.collectIsDraggedAsState()


        val clickable = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { }


        Slider(
            value = sliderValue.toFloat(),
            onValueChange = { newValue ->
                if (isDragged && isPlaying) {
                    viewModel.pause()
                    manuallyStopped = true
                }
                sliderValue = newValue.toLong()
            },
            valueRange = 0.toFloat()..totalTime.toFloat(),
            onValueChangeFinished = {
                if (manuallyStopped) {
                    viewModel.play()
                    manuallyStopped = false
                }
                onValueChangeFinished(sliderValue)
            },
            colors = SliderDefaults.colors(
                thumbColor = Red,
                activeTrackColor = MaterialTheme.colors.secondary,
            ),
            interactionSource = interactionSource,
            modifier = Modifier.then(clickable)
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
    onFavouriteClick: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = {}
        ) {

            Icon(
                imageVector = if (isFavourite) Icons.Filled.RepeatOne
                else Icons.Filled.Repeat,
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
                stringResource(id = R.string.more),
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
            onClick = { onPlayPauseClick() }
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

