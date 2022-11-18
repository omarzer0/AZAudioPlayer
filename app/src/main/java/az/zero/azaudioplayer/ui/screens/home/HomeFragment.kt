@file:OptIn(ExperimentalPagerApi::class)

package az.zero.azaudioplayer.ui.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import az.zero.azaudioplayer.media.player.extensions.EMPTY_AUDIO
import az.zero.azaudioplayer.ui.composables.*
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections.*
import az.zero.azaudioplayer.ui.screens.tab_screens.AlbumScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.AllAudioScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.ArtistScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.PlaylistScreen
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.utils.fakeAudio
import az.zero.player.extensions.isPlaying
import com.google.accompanist.pager.ExperimentalPagerApi
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterialApi
@ExperimentalPagerApi
@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            // adding fakeAudio to differentiate between real empty list and getting data from DB
            val allAudios = viewModel.allAudio.observeAsState().value ?: listOf(fakeAudio)
            Log.e("allAudios", "onCreateView: ${allAudios.size}")

            if (allAudios.isNotEmpty()) {
                val tabNames = getTabsName()
                HomeScreen(tabNames, viewModel, findNavController())
            } else {
                EmptyHomeScreen(
                    onBottomTextClick = {
                        findNavController().navigate(
                            actionHomeFragmentToScanLocalFragment()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyHomeScreen(
    onBottomTextClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TopWithBottomText(
            modifier = Modifier.fillMaxWidth(),
            topTextString = stringResource(id = R.string.no_result),
            bottomTextString = stringResource(id = R.string.click_here_to_search_audio_files),
            bottomTextColor = SelectedColor,
            topTextAlign = TextAlign.Center,
            bottomTextAlign = TextAlign.Center,
            bottomTextModifier = Modifier
                .clickableSafeClick {
                    onBottomTextClick()
                }
                .padding(8.dp)
        )
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun HomeScreen(
    tabNames: List<String>,
    viewModel: HomeViewModel,
    navController: NavController,
) {
    var isDropDownExpanded by rememberSaveable { mutableStateOf(false) }
    var tabNumber by remember { mutableStateOf(0) }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        scaffoldState = rememberScaffoldState(),
        topBar = {
            AppBarWithSearch(onSearchClick = {
                val searchDirections = actionHomeFragmentToSearchFragment()
                navController.navigate(searchDirections)
            }, onMoreClick = {
                isDropDownExpanded = true
            }, customDropDownContent = {
                CustomDropdown(
                    isDropDownExpanded = isDropDownExpanded,
                    onDismissDropDown = { isDropDownExpanded = false },
                    dropDownItems = getDropdownActions(LocalContext.current, tabNumber),
                    onActionClick = { action ->
                        isDropDownExpanded = false
                        when (action) {
                            HomeDropdownActions.ManageAudios -> {
                                navController.navigate(actionHomeFragmentToAudioManageFragment())
                            }
                            HomeDropdownActions.Settings -> {
                                navController.navigate(actionHomeFragmentToSettingsFragment())
                            }
                            HomeDropdownActions.ManageAlbums -> {
                                navController.navigate(actionHomeFragmentToAlbumManageFragment())
                            }
                            HomeDropdownActions.ManageArtists -> {
                                navController.navigate(actionHomeFragmentToArtistManageFragment())
                            }
                            HomeDropdownActions.ManagePlaylists -> {
                                navController.navigate(actionHomeFragmentToPlaylistManageFragment())
                            }
                            HomeDropdownActions.SearchLocalAudio -> {
                                navController.navigate(actionHomeFragmentToScanLocalFragment())
                            }
                            HomeDropdownActions.SortAlbumsBy -> {
                                navController.navigate(
                                    actionHomeFragmentToSortAlbumBottomSheetFragment()
                                )
                            }
                            HomeDropdownActions.SortAudiosBy -> {
                                navController.navigate(
                                    actionHomeFragmentToSortAudioBottomSheetFragment()
                                )
                            }
                        }
                    }
                )
            })
        },
        content = {
            TabContent(
                tabNames = tabNames,
                viewModel = viewModel,
                navController = navController,
                onTapChange = {
                    Log.e("BackHandler", "TabChanged: $it" )
                    tabNumber = it
                }
            )
        }
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabContent(
    tabNames: List<String>,
    viewModel: HomeViewModel,
    navController: NavController,
    initialTabPosition: Int = 0,
    onTapChange: (Int) -> Unit,
) {
    Column {
        TextTab(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f),
            listOfTabNames = tabNames,
            initialTabPosition = initialTabPosition,
            tabHostBackgroundColor = MaterialTheme.colors.background,
            tabSelectorColor = SelectedColor,
            selectedContentColor = SelectedColor,
            unSelectedContentColor = MaterialTheme.colors.onPrimary,
            onTapChanged = onTapChange,
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
                0 -> AllAudioScreen(viewModel)
                1 -> ArtistScreen(viewModel, navController)
                2 -> AlbumScreen(viewModel, navController)
                3 -> PlaylistScreen(viewModel, navController)
            }
        }

        HomeBottomPlayer(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
fun HomeBottomPlayer(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    navController: NavController,
) {

    val currentPlayingAudio = viewModel.currentPlayingAudio.observeAsState()
    val audio = currentPlayingAudio.value ?: EMPTY_AUDIO
    val playingState = viewModel.playbackState.observeAsState()
    val isPlaying = playingState.value?.isPlaying ?: false
    val enabled = audio.data.isNotEmpty()

    BottomPlayer(
        modifier = modifier,
        audio = audio,
        enabled = enabled,
        isPlaying = isPlaying,
        onBodyClick = { navController.navigate(actionGlobalPlayerBottomSheetFragment()) },
        onFavouriteClick = { viewModel.addOrRemoveFromFavourite(audio) },
        onPlayOrPauseClick = { viewModel.playOrPause() }
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

sealed class HomeDropdownActions {
    object SearchLocalAudio : HomeDropdownActions()
    object Settings : HomeDropdownActions()
    object SortAudiosBy : HomeDropdownActions()
    object ManageAudios : HomeDropdownActions()
    object ManageArtists : HomeDropdownActions()
    object SortAlbumsBy : HomeDropdownActions()
    object ManageAlbums : HomeDropdownActions()
    object ManagePlaylists : HomeDropdownActions()
}

fun getDropdownActions(context: Context, tabNumber: Int): List<Pair<String, HomeDropdownActions>> {
    val list: MutableList<Pair<String, HomeDropdownActions>> = mutableListOf()
    when (tabNumber) {
        0 -> {
            list.add(Pair(context.getString(R.string.search_local_audio),
                HomeDropdownActions.SearchLocalAudio))
            list.add(Pair(context.getString(R.string.sort_audios_by),
                HomeDropdownActions.SortAudiosBy))
            list.add(Pair(context.getString(R.string.manage_audios),
                HomeDropdownActions.ManageAudios))
            list.add(Pair(context.getString(R.string.settings), HomeDropdownActions.Settings))
        }
        1 -> {
            list.add(Pair(context.getString(R.string.search_local_audio),
                HomeDropdownActions.SearchLocalAudio))
            list.add(Pair(context.getString(R.string.manage_artists),
                HomeDropdownActions.ManageArtists))
            list.add(Pair(context.getString(R.string.settings), HomeDropdownActions.Settings))
        }
        2 -> {
            list.add(Pair(context.getString(R.string.search_local_audio),
                HomeDropdownActions.SearchLocalAudio))
            list.add(Pair(context.getString(R.string.sort_albums_by),
                HomeDropdownActions.SortAlbumsBy))
            list.add(Pair(context.getString(R.string.manage_albums),
                HomeDropdownActions.ManageAlbums))
            list.add(Pair(context.getString(R.string.settings), HomeDropdownActions.Settings))
        }
        3 -> {
            list.add(Pair(context.getString(R.string.search_local_audio),
                HomeDropdownActions.SearchLocalAudio))
            list.add(Pair(context.getString(R.string.manage_playlists),
                HomeDropdownActions.ManagePlaylists))
            list.add(Pair(context.getString(R.string.settings), HomeDropdownActions.Settings))
        }
    }
    return list
}

//@file:OptIn(ExperimentalPagerApi::class)
//
//package az.zero.azaudioplayer.ui.screens.home
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.fragment.app.viewModels
//import androidx.navigation.NavController
//import androidx.navigation.fragment.findNavController
//import az.zero.azaudioplayer.R
//import az.zero.azaudioplayer.core.BaseFragment
//import az.zero.azaudioplayer.media.player.extensions.EMPTY_AUDIO
//import az.zero.azaudioplayer.ui.composables.*
//import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections.*
//import az.zero.azaudioplayer.ui.screens.tab_screens.AlbumScreen
//import az.zero.azaudioplayer.ui.screens.tab_screens.AllAudioScreen
//import az.zero.azaudioplayer.ui.screens.tab_screens.ArtistScreen
//import az.zero.azaudioplayer.ui.screens.tab_screens.PlaylistScreen
//import az.zero.azaudioplayer.ui.theme.SelectedColor
//import az.zero.azaudioplayer.utils.fakeAudio
//import az.zero.player.extensions.isPlaying
//import com.google.accompanist.pager.ExperimentalPagerApi
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//
//@ExperimentalMaterialApi
//@ExperimentalPagerApi
//@AndroidEntryPoint
//class HomeFragment : BaseFragment() {
//
//    private val viewModel: HomeViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
//    ): View {
//        return setFragmentContent {
//            // adding fakeAudio to differentiate between real empty list and getting data from DB
//            val allAudios = viewModel.allAudio.observeAsState().value ?: listOf(fakeAudio)
//            Log.e("allAudios", "onCreateView: ${allAudios.size}")
//
//            if (allAudios.isNotEmpty()) {
//                val tabNames = getTabsName()
//                HomeScreen(tabNames, viewModel, findNavController())
//            } else {
//                EmptyHomeScreen(
//                    onBottomTextClick = {
//                        findNavController().navigate(
//                            actionHomeFragmentToScanLocalFragment()
//                        )
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun EmptyHomeScreen(
//    onBottomTextClick: () -> Unit,
//) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        TopWithBottomText(
//            modifier = Modifier.fillMaxWidth(),
//            topTextString = stringResource(id = R.string.no_result),
//            bottomTextString = stringResource(id = R.string.click_here_to_search_audio_files),
//            bottomTextColor = SelectedColor,
//            topTextAlign = TextAlign.Center,
//            bottomTextAlign = TextAlign.Center,
//            bottomTextModifier = Modifier
//                .clickableSafeClick {
//                    onBottomTextClick()
//                }
//                .padding(8.dp)
//        )
//    }
//}
//
//@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
//@ExperimentalMaterialApi
//@ExperimentalPagerApi
//@Composable
//fun HomeScreen(
//    tabNames: List<String>,
//    viewModel: HomeViewModel,
//    navController: NavController,
//) {
//    var isDropDownExpanded by rememberSaveable { mutableStateOf(false) }
//    var tabNumber by remember { mutableStateOf(0) }
//
//    val modalBottomSheetState = rememberModalBottomSheetState(
//        initialValue = ModalBottomSheetValue.Hidden,
//        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
//    )
//    val coroutineScope = rememberCoroutineScope()
//
//    Scaffold(
//        backgroundColor = MaterialTheme.colors.background,
//        scaffoldState = rememberScaffoldState(),
//        topBar = {
//            AppBarWithSearch(onSearchClick = {
//                val searchDirections = actionHomeFragmentToSearchFragment()
//                navController.navigate(searchDirections)
//            }, onMoreClick = {
//                isDropDownExpanded = true
//            }, customDropDownContent = {
//                CustomDropdown(
//                    isDropDownExpanded = isDropDownExpanded,
//                    onDismissDropDown = { isDropDownExpanded = false },
//                    dropDownItems = getDropdownActions(LocalContext.current, tabNumber),
//                    onActionClick = { action ->
//                        isDropDownExpanded = false
//                        when (action) {
//                            HomeDropdownActions.ManageAudios -> {
//                                navController.navigate(actionHomeFragmentToAudioManageFragment())
//                            }
//                            HomeDropdownActions.Settings -> {
//                                navController.navigate(actionHomeFragmentToSettingsFragment())
//                            }
//                            HomeDropdownActions.ManageAlbums -> {
//                                navController.navigate(actionHomeFragmentToAlbumManageFragment())
//                            }
//                            HomeDropdownActions.ManageArtists -> {
//                                navController.navigate(actionHomeFragmentToArtistManageFragment())
//                            }
//                            HomeDropdownActions.ManagePlaylists -> {
//                                navController.navigate(actionHomeFragmentToPlaylistManageFragment())
//                            }
//                            HomeDropdownActions.SearchLocalAudio -> {
//                                navController.navigate(actionHomeFragmentToScanLocalFragment())
//                            }
//                            HomeDropdownActions.SortAlbumsBy -> {
//                                // TODO add bottom sheet
//                            }
//                            HomeDropdownActions.SortAudiosBy -> {
//                                // TODO add bottom sheet
//                                coroutineScope.launch {
//                                    if (modalBottomSheetState.isVisible) modalBottomSheetState.hide()
//                                    else modalBottomSheetState.show()
//                                }
//                            }
//                        }
//                    }
//                )
//            })
//        },
//        content = {
//            MainScreen(
//                content = {
//                    TabContent(
//                        tabNames = tabNames,
//                        viewModel = viewModel,
//                        navController = navController,
//                        onTapChange = {
//                            tabNumber = it
//                        }
//                    )
//                },
//                sheetState = modalBottomSheetState,
//                coroutineScope = coroutineScope
//            )
//        }
//    )
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun MainScreen(
//    sheetState: ModalBottomSheetState,
//    coroutineScope: CoroutineScope,
//    content: @Composable () -> Unit,
//) {
//
//    BackHandler(sheetState.isVisible) {
//        coroutineScope.launch { sheetState.hide() }
//    }
//
//    ModalBottomSheetLayout(
//        sheetState = sheetState,
//        sheetContent = {
//            BottomSheet(
//                onTextClick = {
//                    coroutineScope.launch {
//                        sheetState.hide()
//                    }
//                }
//            )
//        },
//        modifier = Modifier.fillMaxSize()
//    ) {
//        content()
//    }
//}
//
//@Composable
//fun BottomSheet(
//    onTextClick: () -> Unit,
//) {
//    Column(
//        modifier = Modifier.padding(32.dp)
//    ) {
//        Text(
//            text = "Bottom sheet",
//            style = MaterialTheme.typography.h6
//        )
//        Spacer(modifier = Modifier.height(32.dp))
//        Text(
//            modifier = Modifier.clickableSafeClick { onTextClick() },
//            text = "Click outside the bottom sheet to hide it",
//            style = MaterialTheme.typography.body1
//        )
//    }
//}
//
//@Composable
//fun TabContent(
//    tabNames: List<String>,
//    viewModel: HomeViewModel,
//    navController: NavController,
//    onTapChange: (Int) -> Unit,
//) {
//    Column {
//        TextTab(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.8f),
//            listOfTabNames = tabNames,
//            tabHostBackgroundColor = MaterialTheme.colors.background,
//            tabSelectorColor = SelectedColor,
//            selectedContentColor = SelectedColor,
//            unSelectedContentColor = MaterialTheme.colors.onPrimary,
//            onTapChanged = onTapChange,
//            textContent = { text ->
//                Text(
//                    text = text,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 12.sp,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis,
//                    textAlign = TextAlign.Start
//                )
//            }
//        ) {
//            when (it) {
//                0 -> AllAudioScreen(viewModel)
//                1 -> ArtistScreen(viewModel, navController)
//                2 -> AlbumScreen(viewModel, navController)
//                3 -> PlaylistScreen(viewModel, navController)
//            }
//        }
//
//        HomeBottomPlayer(
//            modifier = Modifier.fillMaxWidth(),
//            viewModel = viewModel,
//            navController = navController
//        )
//    }
//}
//
//@Composable
//fun HomeBottomPlayer(
//    modifier: Modifier = Modifier,
//    viewModel: HomeViewModel,
//    navController: NavController,
//) {
//
//    val currentPlayingAudio = viewModel.currentPlayingAudio.observeAsState()
//    val audio = currentPlayingAudio.value ?: EMPTY_AUDIO
//    val playingState = viewModel.playbackState.observeAsState()
//    val isPlaying = playingState.value?.isPlaying ?: false
//    val enabled = audio.data.isNotEmpty()
//
//    BottomPlayer(
//        modifier = modifier,
//        audio = audio,
//        enabled = enabled,
//        isPlaying = isPlaying,
//        onBodyClick = { navController.navigate(actionGlobalPlayerBottomSheetFragment()) },
//        onFavouriteClick = { viewModel.addOrRemoveFromFavourite(audio) },
//        onPlayOrPauseClick = { viewModel.playOrPause() }
//    )
//}
//
//
//@Composable
//fun getTabsName(): List<String> {
//    return listOf(
//        stringResource(id = R.string.audio),
//        stringResource(id = R.string.artist),
//        stringResource(id = R.string.albums),
//        stringResource(id = R.string.playlists)
//    )
//}
//
//sealed class HomeDropdownActions {
//    object SearchLocalAudio : HomeDropdownActions()
//    object Settings : HomeDropdownActions()
//    object SortAudiosBy : HomeDropdownActions()
//    object ManageAudios : HomeDropdownActions()
//    object ManageArtists : HomeDropdownActions()
//    object SortAlbumsBy : HomeDropdownActions()
//    object ManageAlbums : HomeDropdownActions()
//    object ManagePlaylists : HomeDropdownActions()
//}
//
//fun getDropdownActions(context: Context, tabNumber: Int): List<Pair<String, HomeDropdownActions>> {
//    val list: MutableList<Pair<String, HomeDropdownActions>> = mutableListOf()
//    when (tabNumber) {
//        0 -> {
//            list.add(Pair(context.getString(R.string.search_local_audio),
//                HomeDropdownActions.SearchLocalAudio))
//            list.add(Pair(context.getString(R.string.sort_audios_by),
//                HomeDropdownActions.SortAudiosBy))
//            list.add(Pair(context.getString(R.string.manage_audios),
//                HomeDropdownActions.ManageAudios))
//            list.add(Pair(context.getString(R.string.settings), HomeDropdownActions.Settings))
//        }
//        1 -> {
//            list.add(Pair(context.getString(R.string.search_local_audio),
//                HomeDropdownActions.SearchLocalAudio))
//            list.add(Pair(context.getString(R.string.manage_artists),
//                HomeDropdownActions.ManageArtists))
//            list.add(Pair(context.getString(R.string.settings), HomeDropdownActions.Settings))
//        }
//        2 -> {
//            list.add(Pair(context.getString(R.string.search_local_audio),
//                HomeDropdownActions.SearchLocalAudio))
//            list.add(Pair(context.getString(R.string.sort_albums_by),
//                HomeDropdownActions.SortAlbumsBy))
//            list.add(Pair(context.getString(R.string.manage_albums),
//                HomeDropdownActions.ManageAlbums))
//            list.add(Pair(context.getString(R.string.settings), HomeDropdownActions.Settings))
//        }
//        3 -> {
//            list.add(Pair(context.getString(R.string.search_local_audio),
//                HomeDropdownActions.SearchLocalAudio))
//            list.add(Pair(context.getString(R.string.manage_playlists),
//                HomeDropdownActions.ManagePlaylists))
//            list.add(Pair(context.getString(R.string.settings), HomeDropdownActions.Settings))
//        }
//    }
//    return list
//}