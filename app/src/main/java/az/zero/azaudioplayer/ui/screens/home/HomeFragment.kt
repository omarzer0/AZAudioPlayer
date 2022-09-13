package az.zero.azaudioplayer.ui.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import az.zero.azaudioplayer.media.player.extensions.isPlaying
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.screens.tab_screens.*
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.utils.common_composables.TextTab
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.utils.ui_extensions.mirror
import az.zero.base.utils.AudioActions
import az.zero.player.EMPTY_AUDIO
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
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

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun HomeFragmentContent(
    tabNames: List<String>,
    viewModel: HomeViewModel,
    navController: NavController
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        backgroundColor = MaterialTheme.colors.primary,
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        topBar = {
            AppBar(onSearchClick = {
                val searchDirections = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
                navController.navigate(searchDirections)
            }, onMoreClick = {

            })
        }, sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.Blue)
            ) {

            }
        }
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
                        val currentPlayingID =
                            viewModel.currentPlayingAudio.observeAsState().value?.data ?: ""
                        AllAudioScreen(audioList, currentPlayingID,
                            onAudioItemClick = { audio ->
                                viewModel.audioAction(AudioActions.Toggle(audio.data), audioList)
                            },
                            onAudioIconClick = { audio, menuAction ->
                                when (menuAction) {
                                    MenuActionTypeForAllScreen.DELETE -> {

                                    }
                                    MenuActionTypeForAllScreen.EDIT -> {

                                    }
                                }
                            })
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

@Composable
fun AppBar(
    onSearchClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
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
            IconButton(onClick = { onSearchClick() }) {
                Icon(
                    Icons.Filled.Search,
                    stringResource(id = R.string.search),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            IconButton(onClick = { onMoreClick() }) {
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
