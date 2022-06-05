package az.zero.azaudioplayer.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.screens.tab_screens.AlbumScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.AllAudioScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.ArtistScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.PlaylistScreen
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.utils.common_composables.TextTab
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagerApi
@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return setFragmentContent {
            val tabNames = getTabsName()
            HomeFragmentContent(tabNames, viewModel, findNavController())
        }
    }
}

@ExperimentalPagerApi
@Composable
fun HomeFragmentContent(
    tabNames: List<String>,
    viewModel: HomeViewModel,
    navController: NavController
) {
    Scaffold(
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
                    0 -> {
                        val audioList by remember { viewModel.allAudio }.observeAsState()
                        AllAudioScreen(viewModel, audioList, 0)
                    }
                    1 -> ArtistScreen(viewModel, navController)
                    2 -> AlbumScreen(viewModel, navController)
                    3 -> PlaylistScreen(viewModel, navController)
                }
            }
        }

    }
}

@Composable
fun AppBar() {
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