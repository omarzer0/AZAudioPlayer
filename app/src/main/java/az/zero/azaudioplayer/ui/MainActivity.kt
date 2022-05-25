package az.zero.azaudioplayer.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.common_composables.ChangeStatusBarColor
import az.zero.azaudioplayer.ui.common_composables.TextTab
import az.zero.azaudioplayer.ui.screens.AudioScreen
import az.zero.azaudioplayer.ui.theme.AZAudioPlayerTheme
import az.zero.azaudioplayer.ui.theme.SelectedColor
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagerApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AZAudioPlayerTheme {
//                DestinationsNavHost(navGraph = NavGraphs.root)

                val tabNames = getTabsName()

                ChangeStatusBarColor(statusColor = MaterialTheme.colors.primary)

                Scaffold(
                    modifier = Modifier,
                    backgroundColor = MaterialTheme.colors.primary,
                    topBar = {
                        AppBar()
                    },
                ) {
                    Column {
                        TextTab(
                            listOfTabNames = tabNames,
                            tabHostBackgroundColor = MaterialTheme.colors.primary,
                            tabSelectorColor = SelectedColor,
                            selectedContentColor = SelectedColor,
                            unSelectedContentColor = MaterialTheme.colors.onPrimary,
                            textContent = { text ->
                                Text(
                                    text = text,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Start
                                )
                            }
                        ) {
                            when (it) {
                                0 -> AudioScreen()
//                                1 -> AudioScreen()
//                                2 -> AudioScreen()
//                                3 -> AudioScreen()
                            }
                        }
                    }

                }

            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val failedToGrant = permissions.entries.any { !it.value }
            if (failedToGrant) {
                finish()
                return@registerForActivityResult
            }
        }

    override fun onStart() {
        super.onStart()

        activityResultLauncher.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activityResultLauncher.unregister()
    }
}

@Composable
fun AppBar() {
    //https://fonts.google.com/icons?selected=Material+Icons

    TopAppBar(
        title = {
            Text(
                text = stringResource(id = az.zero.azaudioplayer.R.string.app_name),
                color = MaterialTheme.colors.onPrimary
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Filled.Search,
                    stringResource(id = R.string.search),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.Filled.MoreVert,
                    stringResource(id = R.string.more),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
    )
}

@Composable
fun getTabsName(): List<String> {
    return listOf(
        stringResource(id = R.string.audio),
        stringResource(id = R.string.artist),
        stringResource(id = R.string.albums),
        stringResource(id = R.string.playlists)
    )
}
