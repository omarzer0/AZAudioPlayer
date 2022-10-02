package az.zero.azaudioplayer.ui.screens.tab_screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.composables.*
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.ui_utils.clickableSafeClick
import az.zero.azaudioplayer.ui.ui_utils.ui_extensions.mirror
import az.zero.db.entities.DBPlaylist

@Composable
fun PlaylistScreen(
    viewModel: HomeViewModel,
    navController: NavController,
) {

    val allPlaylist = viewModel.allPlaylists.observeAsState().value ?: emptyList()
    val errorAddingDuplicatePlaylistName by viewModel.errorFlow.collectAsState(initial = false)

    if (errorAddingDuplicatePlaylistName) {
        Toast.makeText(
            LocalContext.current,
            stringResource(id = R.string.playlist_already_exists),
            Toast.LENGTH_SHORT
        ).show()
    }

    PlaylistScreen(
        allPlaylist = allPlaylist,
        errorAddingDuplicatePlaylistName = errorAddingDuplicatePlaylistName,
        onPlayListClick = {
            navController.navigate(
                HomeFragmentDirections.actionHomeFragmentToPlaylistDetailsFragment(it.name)
            )
        }, onCreateClickNewPlaylist = { playlistName ->
            // TODO check if name already exists
            viewModel.createANewPlayListIfNotExist(playlistName)
        }
    )

}

@Composable
fun PlaylistScreen(
    allPlaylist: List<DBPlaylist>,
    errorAddingDuplicatePlaylistName: Boolean,
    onPlayListClick: (DBPlaylist) -> Unit,
    onCreateClickNewPlaylist: (String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        var openDialog by rememberSaveable { mutableStateOf(false) }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = allPlaylist) { playlist ->
                PlaylistItem(playlist = playlist) { onPlayListClick(playlist) }
            }

            item {
                Divider(modifier = Modifier.padding(top = 16.dp))
                AddPlayList { openDialog = true }
            }
        }

        CustomDialog(
            openDialog = openDialog,
            onOpenDialogChanged = {
                openDialog = !openDialog
            },
            onCreateClick = { playlistName ->
                onCreateClickNewPlaylist(playlistName)
            }
        )

    }
}

@Composable
fun PlaylistItem(
    modifier: Modifier = Modifier,
    playlist: DBPlaylist,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick {
                onClick()
            }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val image = if (playlist.DBAudioList.isEmpty()) ""
        else playlist.DBAudioList[0].cover


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

        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomText(
            modifier = Modifier.weight(1f),
            topTextName = playlist.name,
            bottomTextName = "${playlist.DBAudioList.size} ${stringResource(id = R.string.audios)}"
        )

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            Icons.Filled.KeyboardArrowRight,
            stringResource(id = R.string.more),
            tint = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
            modifier = Modifier.mirror()
        )
    }
}

@Composable
fun CustomDialog(
    openDialog: Boolean,
    onOpenDialogChanged: () -> Unit,
    onCreateClick: (playlistName: String) -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    var toast: Toast? by remember { mutableStateOf(null) }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                text = ""
                onOpenDialogChanged()
            },
            text = {
                CustomEditText(
                    text = text,
                    hint = "PlayListName",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    text = it
                }

            },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (text.isEmpty()) {
                                toast?.cancel()
                                toast = Toast.makeText(
                                    context,
                                    "Name Can't be empty",
                                    Toast.LENGTH_LONG
                                )
                                toast?.show()
                            } else {
                                onOpenDialogChanged()
                                onCreateClick(text)
                                text = ""
                            }
                        }
                    ) {
                        Text("Create", color = MaterialTheme.colors.onPrimary)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            onOpenDialogChanged()
                            text = ""
                        }
                    ) {
                        Text("Cancel", color = MaterialTheme.colors.onPrimary)
                    }
                }
            }
        )
    }
}

@Composable
fun AddPlayList(
    modifier: Modifier = Modifier,
    onAddPlayListClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick { onAddPlayListClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocalImageIcon(
            localImageUrl = Icons.Filled.Add,
            addBorder = false,
            iconTint = SecondaryTextColor,
            imageBackgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier.border(width = 1.dp, SecondaryTextColor, RoundedCornerShape(12.dp)),
            innerImagePadding = 12.dp
        )
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(id = R.string.new_playlist),
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.h2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterVertically)
                .weight(0.6f),
        )
    }
}
