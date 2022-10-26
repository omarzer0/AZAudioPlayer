package az.zero.azaudioplayer.ui.screens.scan_local_files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.ui_utils.ui_extensions.mirror
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanLocalFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            ScanLocalScreen(
                navController = findNavController()
            )
        }
    }
}

@Composable
fun ScanLocalScreen(
    navController: NavController,
) {
    ScanLocalScreen(
        onBackPressed = { navController.navigateUp() },
        onScanSettingsClick = {},
        onScanAnotherTimeClick = {}
    )
}

@Composable
fun ScanLocalScreen(
    onBackPressed: () -> Unit,
    onScanSettingsClick: () -> Unit,
    onScanAnotherTimeClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ScanLocalHeader(onBackPressed = onBackPressed, onScanSettingsClick = onScanSettingsClick)

        ScanAnimation()

        TopWithBottomText(
            topTextString = stringResource(id = R.string.scan_completed),
            topTextStyle = MaterialTheme.typography.h2.copy(color = if (isSystemInDarkTheme()) Color.White else Color.Black),
            topTextAlign = TextAlign.Center,
            bottomTextString = stringResource(id = R.string.scan_completed),
            bottomTextStyle = MaterialTheme.typography.body1.copy(color = if (isSystemInDarkTheme()) Color.DarkGray else Color.Gray),
            bottomTextAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier.width(250.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            shape = CircleShape,
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
fun ScanAnimation() {
    Box(
        modifier = Modifier
            .size(300.dp)
            .background(Color.Blue)
    )
}

@Composable
fun ScanLocalHeader(
    onBackPressed: () -> Unit,
    onScanSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.scan_local_audio_files),
                color = MaterialTheme.colors.onPrimary
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        actions = {
            IconButton(onClick = { onScanSettingsClick() }) {
                Icon(
                    Icons.Filled.Settings,
                    stringResource(id = R.string.settings),
                    tint = MaterialTheme.colors.onBackground
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { onBackPressed() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    stringResource(id = R.string.back),
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.mirror()
                )
            }
        }

    )
}