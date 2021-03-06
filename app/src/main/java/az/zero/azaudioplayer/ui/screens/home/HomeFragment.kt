package az.zero.azaudioplayer.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import az.zero.azaudioplayer.media.player.EMPTY_AUDIO
import az.zero.azaudioplayer.media.player.extensions.isPlaying
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.screens.player.PlayerBottomSheetFragment
import az.zero.azaudioplayer.ui.screens.tab_screens.AlbumScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.AllAudioScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.ArtistScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.PlaylistScreen
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.utils.common_composables.TextTab
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror
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
        topBar = { AppBar() },
    ) {
        Column {
            TextTab(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
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
                        val audioList = viewModel.allAudio.observeAsState().value
                        AllAudioScreen(viewModel, audioList, 0)
                    }
                    1 -> ArtistScreen(viewModel, navController)
                    2 -> AlbumScreen(viewModel, navController)
                    3 -> PlaylistScreen(viewModel, navController)
                }
            }

            BottomPlayer(
                modifier = Modifier.fillMaxWidth(),
                viewModel = viewModel
            ) {
                navController.navigate(HomeFragmentDirections.actionGlobalPlayerBottomSheetFragment())
            }
        }

    }
}

@Composable
fun BottomPlayer(modifier: Modifier = Modifier, viewModel: HomeViewModel, onBodyClick: () -> Unit) {
    Column(
        modifier = modifier.clickable { onBodyClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val currentPlayingAudio = viewModel.currentPlayingAudio.observeAsState()
        val audio = currentPlayingAudio.value ?: EMPTY_AUDIO
        val playingState = viewModel.playbackState.observeAsState()
        val isPlaying = playingState.value?.isPlaying ?: false

        Spacer(
            modifier = Modifier
                .height(0.5.dp)
                .fillMaxWidth()
                .background(Color.LightGray)
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
                modifier = Modifier
                    .weight(0.1f)
                    .mirror(), onClick = {
                    if (isPlaying) viewModel.audioAction(AudioActions.Pause)
                    else viewModel.audioAction(AudioActions.Toggle(audio.data))
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