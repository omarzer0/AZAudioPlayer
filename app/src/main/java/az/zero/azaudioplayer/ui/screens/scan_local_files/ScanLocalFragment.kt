package az.zero.azaudioplayer.ui.screens.scan_local_files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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

    val state = viewModel.state.collectAsState().value

    ScanLocalScreen(
        isRunning = state.scanState == LOADING,
        scanState = state.scanState,
        audiosFound = state.audiosFound,
        skipRecordingsDirectoryAudios = state.skipRecordingsDirectoryAudios,
        skipAndroidDirectoryAudios = state.skipAndroidDirectoryAudios,
        onBackPressed = { navController.navigateUp() },
        onScanAnotherTimeClick = { viewModel.onSearchClick() },
        onSkipRecordingsDirectoryChange = { viewModel.onSkipRecordingsDirectoryChange(it) },
        onSkipAndroidDirectoryChange = { viewModel.onSkipAndroidDirectoryChange(it) }
    )
}

@Composable
fun ScanLocalScreen(
    isRunning: Boolean,
    scanState: ScanLocalState.ScanState,
    audiosFound: Int,
    skipRecordingsDirectoryAudios: Boolean,
    skipAndroidDirectoryAudios: Boolean,
    onBackPressed: () -> Unit,
    onScanAnotherTimeClick: () -> Unit,
    onSkipRecordingsDirectoryChange: (isChecked: Boolean) -> Unit,
    onSkipAndroidDirectoryChange: (isChecked: Boolean) -> Unit,
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

        SwitchWithText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.skip_files_of_recordings),
            isChecked = skipRecordingsDirectoryAudios,
            enabled = !isRunning,
            onCheckedChange = onSkipRecordingsDirectoryChange
        )

        SwitchWithText(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.skip_files_of_android_system),
            isChecked = skipAndroidDirectoryAudios,
            enabled = !isRunning,
            onCheckedChange = onSkipAndroidDirectoryChange
        )

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
            enabled = !isRunning,
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
fun SwitchWithText(
    modifier: Modifier = Modifier,
    text: String,
    isChecked: Boolean,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.h2.copy(
        color = MaterialTheme.colors.onBackground
    ),
    onCheckedChange: (isChecked: Boolean) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = text, style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
        )

        Switch(
            checked = isChecked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SelectedColor,
                uncheckedThumbColor = MaterialTheme.colors.onBackground
            )
        )
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