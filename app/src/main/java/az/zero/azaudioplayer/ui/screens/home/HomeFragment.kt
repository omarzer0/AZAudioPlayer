package az.zero.azaudioplayer.ui.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.AppBarWithSearch
import az.zero.azaudioplayer.ui.composables.BottomPlayer
import az.zero.azaudioplayer.ui.composables.CustomDropdown
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections.*
import az.zero.azaudioplayer.ui.screens.tab_screens.AlbumScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.AllAudioScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.ArtistScreen
import az.zero.azaudioplayer.ui.screens.tab_screens.PlaylistScreen
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.ui_utils.TextTab
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
            val allAudios = viewModel.allAudio.observeAsState().value ?: emptyList()
            Log.e("allAudios", "onCreateView: ${allAudios.size}")
            if (allAudios.isNotEmpty()) {
                val tabNames = getTabsName()
                HomeScreen(tabNames, viewModel, findNavController())
                Log.e("allAudios", "if: ${allAudios.size}")
            } else {
                Log.e("allAudios", "else: ${allAudios.size}")
                EmptyHomeScreen()
            }
        }
    }
}

@Composable
fun EmptyHomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red)
    )
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
                                navController.navigate(actionHomeFragmentToScanLocalFragment())
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
                                actionHomeFragmentToScanLocalFragment()
                            }
                            HomeDropdownActions.SortAlbumsBy -> {
                                // TODO add bottom sheet
                            }
                            HomeDropdownActions.SortAudiosBy -> {
                                // TODO add bottom sheet
                            }
                        }
                    }
                )
            })
        }
    ) {
        Column {
            TextTab(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f),
                listOfTabNames = tabNames,
                tabHostBackgroundColor = MaterialTheme.colors.background,
                tabSelectorColor = SelectedColor,
                selectedContentColor = SelectedColor,
                unSelectedContentColor = MaterialTheme.colors.onPrimary,
                onTapChanged = {
                    tabNumber = it
                    Log.d("HomeScreen", "$it")

                },
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

            BottomPlayer(
                modifier = Modifier.fillMaxWidth(),
                viewModel = viewModel
            ) {
                navController.navigate(actionGlobalPlayerBottomSheetFragment())
            }
        }


    }
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