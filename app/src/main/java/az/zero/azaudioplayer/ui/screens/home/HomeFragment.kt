package az.zero.azaudioplayer.ui.screens.home

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import az.zero.azaudioplayer.ui.composables.AppBar
import az.zero.azaudioplayer.ui.composables.BottomPlayer
import az.zero.azaudioplayer.ui.composables.CustomDropdown
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

@ExperimentalMaterialApi
@ExperimentalPagerApi
@Composable
fun HomeScreen(
    tabNames: List<String>,
    viewModel: HomeViewModel,
    navController: NavController,
) {
    var isDropDownExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        backgroundColor = MaterialTheme.colors.primary,
        scaffoldState = rememberScaffoldState(),
        topBar = {
            AppBar(onSearchClick = {
                val searchDirections = HomeFragmentDirections.actionHomeFragmentToSearchFragment()
                navController.navigate(searchDirections)
            }, onMoreClick = {
                isDropDownExpanded = true
            }, customDropDownContent = {
                CustomDropdown(
                    isDropDownExpanded = isDropDownExpanded,
                    onDismissDropDown = { isDropDownExpanded = false },
                    dropDownItems = getDropdownActions(),
                    onActionClick = { action ->
                        isDropDownExpanded = false
                        when (action) {
                            X.AddAudio -> {}
                            X.Delete -> {}
                            X.Rename -> {}
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
                navController.navigate(HomeFragmentDirections.actionGlobalPlayerBottomSheetFragment())
            }
        }


    }
}


sealed class X {
    object AddAudio : X()
    object Rename : X()
    object Delete : X()
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

fun getDropdownActions(): List<Pair<String, X>> = listOf(
    Pair("Add", X.AddAudio),
    Pair("Rename", X.Rename),
    Pair("Delete", X.Delete)
)
