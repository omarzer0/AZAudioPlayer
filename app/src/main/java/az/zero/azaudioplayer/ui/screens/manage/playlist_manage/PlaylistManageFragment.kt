package az.zero.azaudioplayer.ui.screens.manage.playlist_manage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.data.local.model.playlist.SelectablePlaylist
import az.zero.azaudioplayer.ui.composables.BasicHeaderWithBackBtn
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.composables.clickableSafeClick
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistManageFragment : BaseFragment() {

    private val viewModel: PlaylistManageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            ManagePlaylistsScreen(
                viewModel = viewModel,
                navController = findNavController()
            )
        }
    }
}

@Composable
fun ManagePlaylistsScreen(
    viewModel: PlaylistManageViewModel,
    navController: NavController,
) {
    val state = viewModel.state.observeAsState().value ?: PlaylistManageState()

    ManagePlaylistsScreen(
        selectablePlaylists = state.selectablePlaylists,
        areActionsEnabled = state.areActionsEnabled,
        isAllSelected = state.allListSelected,
        isDeleteDialogShown = state.deleteDialogShown,
        onBackPressed = { navController.navigateUp() },
        onPlaylistSelect = { playlistId: String, isSelected: Boolean ->
            viewModel.onPlaylistSelect(playlistId, isSelected)
        },
        onSelectAllPressed = {
            viewModel.onSelectAllButtonClick(it)
        },
        onDeleteAllSelected = {
            viewModel.onDeleteAllSelectedClick()
        }, onDeleteConfirm = {
            viewModel.onDeleteConfirmed()
        },
        onDismissDialog = {
            viewModel.dismissDialog()
        }
    )
}

@Composable
fun ManagePlaylistsScreen(
    modifier: Modifier = Modifier,
    selectablePlaylists: List<SelectablePlaylist>,
    areActionsEnabled: Boolean,
    isAllSelected: Boolean,
    isDeleteDialogShown: Boolean,
    onPlaylistSelect: (playlistId: String, isSelected: Boolean) -> Unit,
    onBackPressed: () -> Unit,
    onSelectAllPressed: (isActive: Boolean) -> Unit,
    onDeleteAllSelected: () -> Unit,
    onDismissDialog: () -> Unit,
    onDeleteConfirm: () -> Unit,
) {

    val shouldShowActions = selectablePlaylists.isNotEmpty()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        BasicHeaderWithBackBtn(
            text = stringResource(id = R.string.manage_playlists),
            onBackPressed = onBackPressed,
            actions = {
                if (shouldShowActions) {
                    Checkbox(
                        checked = isAllSelected,
                        onCheckedChange = { onSelectAllPressed(isAllSelected) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SelectedColor,
                            checkmarkColor = Color.White
                        ),
                    )
                }
            }
        )

        if (shouldShowActions) {
            CustomPlaylistDialog(
                openDialog = isDeleteDialogShown,
                onDismiss = onDismissDialog,
                onConfirmClick = onDeleteConfirm
            )

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(selectablePlaylists, key = { it.playlist.name }) {
                    SelectablePlaylistItem(
                        onPlaylistSelect = { playlistId: String, isSelected: Boolean ->
                            onPlaylistSelect(playlistId, isSelected)
                        },
                        selectablePlaylist = it
                    )
                }
            }

            BottomActionBar(
                onDeleteAllSelected = onDeleteAllSelected,
                isEnabled = areActionsEnabled
            )
        } else {
            EmptyManageScreen()
        }
    }
}

@Composable
fun EmptyManageScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.no_playlist_to_show),
            style = MaterialTheme.typography.h1.copy(
                color = SecondaryTextColor
            )
        )
    }
}

@Composable
fun SelectablePlaylistItem(
    modifier: Modifier = Modifier,
    onPlaylistSelect: (playlistId: String, isSelected: Boolean) -> Unit,
    selectablePlaylist: SelectablePlaylist,
) {
    val playlist = selectablePlaylist.playlist

    LaunchedEffect(selectablePlaylist.isSelected) {
        Log.e("onPlaylistSelect", "selectedUI: ${selectablePlaylist.isSelected}")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick {
                onPlaylistSelect(playlist.name, selectablePlaylist.isSelected)
            }
            .padding(start = 16.dp, bottom = 8.dp, top = 8.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val image = if (playlist.dbAudioList.isEmpty()) ""
        else playlist.dbAudioList[0].cover

        CustomImage(
            modifier = Modifier.size(48.dp),
            image = image
        )

        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomText(
            modifier = Modifier.weight(1f),
            topTextString = playlist.name,
            bottomTextString = "${playlist.dbAudioList.size} ${stringResource(id = R.string.audios)}"
        )

        Spacer(modifier = Modifier.width(16.dp))

        Checkbox(
            checked = selectablePlaylist.isSelected,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = SelectedColor,
                checkmarkColor = Color.White
            )
        )
    }
}


@Composable
fun BottomActionBar(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onDeleteAllSelected: () -> Unit,
) {
    val color = if (isEnabled) MaterialTheme.colors.onBackground
    else SecondaryTextColor

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick(enabled = isEnabled) {
                if (isEnabled) onDeleteAllSelected()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(color = SecondaryTextColor)
        Icon(
            modifier = modifier.padding(8.dp),
            tint = color,
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.delete),
        )
        Text(
            text = stringResource(id = R.string.delete),
            style = MaterialTheme.typography.body1.copy(
                color = color
            )
        )
    }
}

@Composable
fun CustomPlaylistDialog(
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
                    text = stringResource(id = R.string.are_you_sure_you_want_to_delete_all_selected),
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
