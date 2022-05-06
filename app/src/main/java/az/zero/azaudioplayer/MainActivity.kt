package az.zero.azaudioplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.ui.common_composables.ChangeStatusBarColor
import az.zero.azaudioplayer.ui.common_composables.TextTab
import az.zero.azaudioplayer.ui.theme.AZAudioPlayerTheme
import az.zero.azaudioplayer.ui.theme.SelectedColor
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalPagerApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AZAudioPlayerTheme {
                val tabNames = getTabsName()

                ChangeStatusBarColor(statusColor = MaterialTheme.colors.primary)

                Scaffold(
                    modifier = Modifier,
                    backgroundColor = MaterialTheme.colors.background,
                    topBar = {
                        AppBar()
                    },
                ) {
                    Box {
                        TextTab(
                            listOfTabNames = tabNames,
                            tabHostBackgroundColor = MaterialTheme.colors.primary,
                            tabSelectorColor = SelectedColor,
                            selectedContentColor = SelectedColor,
                            unSelectedContentColor = MaterialTheme.colors.onPrimary,
                            textContent = { text ->
                                Text(
                                    text = text,
                                    fontSize = MaterialTheme.typography.h4.fontSize,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        ) {

                        }
                    }
                }

            }
        }
    }
}

@Composable
fun AppBar() {
    //https://fonts.google.com/icons?selected=Material+Icons

    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
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