package az.zero.azaudioplayer.ui.screens.scan_local_files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.BasicHeaderWithBackBtn
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.screens.scan_local_files.ScanLocalState.ScanState.*
import az.zero.azaudioplayer.ui.theme.SelectedColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanLocalFragment : BaseFragment() {

    private val viewModel: ScanLocalViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            ScanLocalScreen(
                viewModel = viewModel,
                navController = findNavController()
            )
        }
    }
}

@Composable
fun ScanLocalScreen(
    viewModel: ScanLocalViewModel,
    navController: NavController,
) {

    val state = viewModel.state.collectAsState(initial = ScanLocalState()).value

    ScanLocalScreen(
        isRunning = state.scanState == LOADING,
        scanState = state.scanState,
        audiosFound = state.audiosFound,
        onBackPressed = { navController.navigateUp() },
        onScanAnotherTimeClick = { viewModel.onSearchClick() }
    )
}

@Composable
fun ScanLocalScreen(
    isRunning: Boolean,
    scanState: ScanLocalState.ScanState,
    audiosFound: Int,
    onBackPressed: () -> Unit,
    onScanAnotherTimeClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        val topText = when (scanState) {
            LOADING -> stringResource(id = R.string.loading___)
            DONE -> stringResource(id = R.string.scan_completed)
            INITIAL -> ""
        }

        val bottomText = when (scanState) {
            LOADING -> stringResource(id = R.string.scanning_for_audios)
            DONE -> "$audiosFound ${stringResource(id = R.string.audios)} ${stringResource(id = R.string.was_found)}"
            INITIAL -> ""
        }

        BasicHeaderWithBackBtn(
            text = stringResource(id = R.string.scan_local_audio_files),
            onBackPressed = onBackPressed
        )

        ScanAnimation(isRunning = isRunning)

        TopWithBottomText(
            topTextString = topText,
            topTextStyle = MaterialTheme.typography.h2.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
            topTextAlign = TextAlign.Center,
            bottomTextString = bottomText,
            bottomTextStyle = MaterialTheme.typography.body1.copy(color = if (isSystemInDarkTheme()) Color.DarkGray else Color.Gray),
            bottomTextAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier.width(250.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            shape = CircleShape,
            enabled = scanState != LOADING,
            colors = ButtonDefaults.buttonColors(backgroundColor = SelectedColor),
            onClick = onScanAnotherTimeClick
        ) {
            Text(
                text = stringResource(id = R.string.scan_again),
                style = MaterialTheme.typography.body1.copy(color = Color.White),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
fun ScanAnimation(
    isRunning: Boolean = false,
) {
    Box(
        modifier = Modifier
            .size(300.dp)
    ) {
        if (isRunning) PulsingAnimation()
        else CheckMarkAnimation()
    }
}

@Composable
fun PulsingAnimation(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        val animationDuration = 1200
        val infiniteTransition = rememberInfiniteTransition()

        val smallCircle by infiniteTransition.animateValue(
            initialValue = 120.dp,
            targetValue = 180.dp,
            Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(animationDuration, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BasicCircle(
                size = 180.dp,
                color = SelectedColor.copy(alpha = 0.25f)
            )
            BasicCircle(
                size = smallCircle,
                color = SelectedColor.copy(alpha = 0.25f)
            )

            Icon(
                modifier = Modifier.size(80.dp),
                painter = painterResource(id = R.drawable.ic_music),
                tint = Color.White,
                contentDescription = ""
            )
        }
    }
}

@Composable
fun CheckMarkAnimation() {
    Column {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BasicCircle(
                size = 180.dp,
                color = SelectedColor.copy(alpha = 0.50f)
            )

            Icon(
                modifier = Modifier.size(80.dp),
                imageVector = Icons.Filled.Check,
                tint = Color.White,
                contentDescription = ""
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun expandFading(time: Int) =
    fadeIn(animationSpec = tween(time * 3)) with
            fadeOut(animationSpec = tween(time))

@OptIn(ExperimentalAnimationApi::class)
private fun expandSizing(time: Int) =
    SizeTransform { initialSize, targetSize ->
        keyframes {
            // Expand to target width first
            IntSize(targetSize.width, initialSize.height) at time
            // Then expand to target height
            durationMillis = time * 3
        }
    }

@OptIn(ExperimentalAnimationApi::class)
private fun shrinkSizing(time: Int) =
    SizeTransform { initialSize, targetSize ->
        keyframes {
            // Shrink to target height first
            IntSize(initialSize.width, targetSize.height) at time
            // Then shrink to target width
            durationMillis = time * 3
        }
    }

@OptIn(ExperimentalAnimationApi::class)
fun shrinkFading(time: Int) =
    fadeIn(animationSpec = tween(time, time * 2)) with
            fadeOut(animationSpec = tween(time * 3))

@Composable
fun BasicCircle(
    size: Dp,
    color: Color = Color.White,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.LightGray.copy(alpha = 0.0f),
) {

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                color
            )
            .border(borderWidth, borderColor)
            .wrapContentSize(Alignment.Center)
    )
}