package az.zero.azaudioplayer.ui.screens.details.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.*
import az.zero.azaudioplayer.ui.screens.details.playlist.PlayListFavMoreActions.ClearFavList
import az.zero.azaudioplayer.ui.screens.details.playlist.PlayListMoreClickActions.*
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.tab_screens.AudioItem
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.utils.fakeAudio
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import az.zero.db.entities.DBPlaylist
import az.zero.player.extensions.EMPTY_AUDIO
import az.zero.player.extensions.isPlaying
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailsFragment : BaseFragment() {
    private val viewModel: PlaylistDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            val playlist =
                viewModel.playlist.observeAsState().value ?: DBPlaylist("", listOf(fakeAudio))

            PlaylistDetailsScreen(
                playlist = playlist,
                viewModel = viewModel,
                navController = findNavController()
            )
        }
    }
}

@Composable
fun PlaylistDetailsScreen(
    playlist: DBPlaylist,
    viewModel: PlaylistDetailsViewModel,
    navController: NavController,
) {
    val selectedId = viewModel.currentPlayingAudio.observeAsState().value?.data ?: ""

    var isDropDownExpanded by rememberSaveable { mutableStateOf(false) }

    var dialogIsOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val currentlyPlayingAudio = viewModel.currentPlayingAudio.observeAsState().value ?: EMPTY_AUDIO
    val isPlaying = viewModel.playbackState.observeAsState().value?.isPlaying ?: false
    val enabled = currentlyPlayingAudio.data.isNotEmpty()


    PlaylistDetailsScreen(
        modifier = Modifier.background(MaterialTheme.colors.background),
        currentlyPlayingAudio = currentlyPlayingAudio,
        isPlaying = isPlaying,
        enabled = enabled,
        playlist = playlist,
        selectedId = selectedId,
        isDropDownExpanded = isDropDownExpanded,
        playAllHeaderEnabled = playlist.dbAudioList.isNotEmpty(),
        onPlayAllClick = {
            viewModel.audioAction(
                action = AudioActions.PlayAll,
                newAudioList = playlist.dbAudioList
            )
        },
        onBackIconClick = { navController.navigateUp() },
        onBodyClick = { navController.navigate(HomeFragmentDirections.actionGlobalPlayerBottomSheetFragment()) },
        onPlayOrPauseClick = { viewModel.playOrPause() },
        onFavouriteClick = { viewModel.addOrRemoveFromFavourite(currentlyPlayingAudio) },
        onMoreClick = { isDropDownExpanded = true },
        onDismissDropDown = { isDropDownExpanded = false },
        onAudioItemClick = { audio ->
            viewModel.audioAction(
                action = AudioActions.Toggle(audio.data),
                newAudioList = playlist.dbAudioList
            )
        },
        onAudioIconClick = { dbAudio, menuActionType ->

        },
        onBottomTextClick = {
            navigateToAddAudioScreen(navController = navController, playlistName = playlist.name)
        },
        onPlayListMoreClickActions = { actions ->
            isDropDownExpanded = false
            when (actions) {
                AddAudio -> {
                    navigateToAddAudioScreen(
                        navController = navController,
                        playlistName = playlist.name
                    )
                }
                Delete -> {
                    dialogIsOpen = true
                }
                Rename -> {
                    // TODO navigate to change name fragment
                }
            }
        }, onFavMoreClickActions = { actions ->
            isDropDownExpanded = false
            when (actions) {
                ClearFavList -> {
                    viewModel.clearFavList()
                }
            }
        }
    )

    CustomDialog(
        openDialog = dialogIsOpen,
        onDismiss = { dialogIsOpen = false },
        onConfirmClick = {
            dialogIsOpen = false
            viewModel.deleteCurrentPlayList()
            navController.navigateUp()
        }
    )
}

@Composable
fun PlaylistDetailsScreen(
    modifier: Modifier = Modifier,
    currentlyPlayingAudio: DBAudio,
    isPlaying: Boolean,
    enabled: Boolean,
    playAllHeaderEnabled: Boolean = true,
    onPlayAllClick: () -> Unit,
    onBodyClick: () -> Unit,
    onPlayOrPauseClick: () -> Unit,
    onFavouriteClick: (Boolean) -> Unit,
    playlist: DBPlaylist,
    selectedId: String,
    isDropDownExpanded: Boolean,
    onMoreClick: () -> Unit,
    onAudioItemClick: (DBAudio) -> Unit,
    onAudioIconClick: (DBAudio, MenuActionType) -> Unit,
    onBottomTextClick: () -> Unit,
    onBackIconClick: () -> Unit,
    onDismissDropDown: () -> Unit,
    onPlayListMoreClickActions: (actions: PlayListMoreClickActions) -> Unit,
    onFavMoreClickActions: (actions: PlayListFavMoreActions) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
    ) {

        BasicHeaderWithBackBtn(
            text = playlist.name,
            onBackPressed = onBackIconClick,
            actions = {
                if (playlist.isFavouritePlaylist) {
                    FavMoreDropDown(
                        isDropDownExpanded = isDropDownExpanded,
                        onDismissDropDown = onDismissDropDown,
                        onPlayListMoreClickActions = onFavMoreClickActions
                    )
                } else {
                    MoreDropDown(
                        isDropDownExpanded = isDropDownExpanded,
                        onDismissDropDown = onDismissDropDown,
                        onPlayListMoreClickActions = onPlayListMoreClickActions
                    )
                }

                IconButton(onClick = { onMoreClick() }) {
                    Icon(
                        Icons.Filled.MoreVert,
                        stringResource(id = R.string.more),
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        )

        PlaylistDetailsSubHeader(playlist = playlist)

        if (playlist.dbAudioList.isEmpty()) {
            EmptyPlaylistDetails(
                isFavPlaylist = playlist.isFavouritePlaylist,
                onBottomTextClick = onBottomTextClick
            )
        } else {
            val realPlaylist by remember(playlist) {
                mutableStateOf(
                    playlist.copy(
                        dbAudioList = playlist.dbAudioList.filter { it.id != fakeAudio.id }
                    )
                )
            }

            PlaylistDetails(
                playlist = realPlaylist,
                selectedId = selectedId,
                playAllHeaderEnabled = playAllHeaderEnabled,
                onPlayAllClick = onPlayAllClick,
                onAudioItemClick = onAudioItemClick,
                onAudioIconClick = onAudioIconClick,
            )
        }

        BottomPlayer(
            currentlyPlayingAudio = currentlyPlayingAudio,
            enabled = enabled,
            isPlaying = isPlaying,
            onBodyClick = onBodyClick,
            onFavouriteClick = onFavouriteClick,
            onPlayOrPauseClick = onPlayOrPauseClick
        )
    }
}

@Composable
fun ColumnScope.EmptyPlaylistDetails(
    isFavPlaylist: Boolean,
    onBottomTextClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LocalImage(
            modifier = Modifier.size(60.dp),
            localImageUrl = R.drawable.ic_music
        )

        Spacer(modifier = Modifier.height(16.dp))

        val bottomText = if (isFavPlaylist) "" else stringResource(id = R.string.add_audio)

        TopWithBottomText(
            modifier = Modifier.fillMaxWidth(),
            topTextString = stringResource(id = R.string.no_result),
            topTextColor = SecondaryTextColor,
            bottomTextString = bottomText,
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

@Composable
fun ColumnScope.PlaylistDetails(
    selectedId: String,
    playlist: DBPlaylist,
    playAllHeaderEnabled: Boolean = true,
    onPlayAllClick: () -> Unit,
    onAudioItemClick: (DBAudio) -> Unit,
    onAudioIconClick: (DBAudio, MenuActionType) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.weight(1f)
    ) {
        item {
            val headerText =
                "${playlist.dbAudioList.size} ${stringResource(id = R.string.of_audios)}"
            PlayAllHeader(
                text = headerText,
                playAllHeaderEnabled = playAllHeaderEnabled,
                onClick = onPlayAllClick
            )
        }

        items(playlist.dbAudioList, key = { it.data }) { audio ->
            AudioItem(
                dbAudio = audio,
                isSelected = audio.data == selectedId,
                onClick = { onAudioItemClick(audio) },
                onIconClick = { menuAction ->
                    onAudioIconClick(audio, menuAction)
                }
            )
        }
    }
}

sealed class PlayListMoreClickActions {
    object AddAudio : PlayListMoreClickActions()
    object Rename : PlayListMoreClickActions()
    object Delete : PlayListMoreClickActions()
}

sealed class PlayListFavMoreActions {
    object ClearFavList : PlayListFavMoreActions()
}

@Composable
fun MoreDropDown(
    isDropDownExpanded: Boolean,
    onDismissDropDown: () -> Unit,
    onPlayListMoreClickActions: (actions: PlayListMoreClickActions) -> Unit,
) {
    DropdownMenu(
        expanded = isDropDownExpanded,
        onDismissRequest = onDismissDropDown
    ) {
        DropdownMenuItem(onClick = {
            onPlayListMoreClickActions(AddAudio)
        }) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.add_audio),
                textAlign = TextAlign.Center
            )
        }

        DropdownMenuItem(onClick = {
            onPlayListMoreClickActions(Rename)
        }) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.rename),
                textAlign = TextAlign.Center
            )
        }

        DropdownMenuItem(onClick = {
            onPlayListMoreClickActions(Delete)
        }) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.delete),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FavMoreDropDown(
    isDropDownExpanded: Boolean,
    onDismissDropDown: () -> Unit,
    onPlayListMoreClickActions: (actions: PlayListFavMoreActions) -> Unit,
) {
    DropdownMenu(
        expanded = isDropDownExpanded,
        onDismissRequest = onDismissDropDown
    ) {
        DropdownMenuItem(onClick = {
            onPlayListMoreClickActions(ClearFavList)
        }) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.clear),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PlaylistDetailsSubHeader(
    playlist: DBPlaylist,
) {
    val image = if (playlist.dbAudioList.isEmpty()) ""
    else playlist.dbAudioList[0].cover

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (playlist.isFavouritePlaylist) {
            LocalImage(
                modifier = Modifier.size(48.dp),
                localImageUrl = R.drawable.ic_fav,
                imageBackgroundColor = SelectedColor
            )
        } else {
            CustomImage(
                modifier = Modifier.size(48.dp),
                image = image
            )
        }

        Spacer(
            modifier = Modifier.width(16.dp)
        )

        TopWithBottomText(
            topTextString = playlist.name,
            bottomTextString = "${playlist.dbAudioList.size} ${stringResource(id = R.string.audios)}"
        )

    }
}


@Composable
fun CustomDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    val textBtnColor =
        if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else Color.DarkGray

    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Text(
                    style = MaterialTheme.typography.h2,
                    text = stringResource(id = R.string.are_you_sure_you_want_to_delete_it),
                )
            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onConfirmClick,
                    ) {
                        Text(stringResource(id = R.string.delete), color = SelectedColor)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(id = R.string.cancel), color = textBtnColor)
                    }
                }
            }
        )
    }
}

private fun navigateToAddAudioScreen(navController: NavController, playlistName: String) {
    navController.navigate(
        PlaylistDetailsFragmentDirections.actionPlaylistDetailsFragmentToAddAudioToPlaylistFragment(
            playlistName
        )
    )
}