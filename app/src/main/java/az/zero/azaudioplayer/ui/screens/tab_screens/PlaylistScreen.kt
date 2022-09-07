package az.zero.azaudioplayer.ui.screens.tab_screens

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import az.zero.azaudioplayer.domain.models.Playlist
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.composables.CustomEditText
import az.zero.azaudioplayer.ui.composables.LocalImageIcon
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick

@Composable
fun PlaylistScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {

    val allPlaylist = viewModel.allPlaylists.observeAsState().value
    if (allPlaylist.isNullOrEmpty()) return

    Box(modifier = Modifier.fillMaxSize()) {
        var openDialog by rememberSaveable { mutableStateOf(false) }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = allPlaylist, key = { it.name }) { playlist ->
                PlaylistItem(playlist = playlist) {
                    navController.navigate(
                        HomeFragmentDirections.actionHomeFragmentToAlbumDetailsFragment(
                            playlist.audioList.toTypedArray()
                        )
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                AddPlayList {
                    openDialog = true
                }
            }
        }

        CustomDialog(
            openDialog = openDialog,
            onOpenDialogChanged = {
                openDialog = !openDialog
            },
            onCreateClick = { playlistName ->
                // TODO check if name already exists
                viewModel.createANewPlayList(playlistName)
            }
        )

    }
}

@Composable
fun CustomDialog(
    openDialog: Boolean,
    onOpenDialogChanged: () -> Unit,
    onCreateClick: (playlistName: String) -> Unit
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
    onAddPlayListClick: () -> Unit
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

@Composable
fun PlaylistItem(playlist: Playlist, onClick: () -> Unit) {
    val image = if (playlist.isFavouritePlaylist) R.drawable.ic_fav else R.drawable.ic_music
    val backgroundColor = if (playlist.isFavouritePlaylist) Color.Red else Color.White

    BasicAudioItem(
        imageUrl = null,
        localImageUrl = image,
        onItemClick = { onClick() },
        topText = playlist.name,
        bottomText = "${playlist.audioList.size} ${stringResource(id = R.string.audios)}",
        iconVector = Icons.Filled.KeyboardArrowRight,
        iconColor = SecondaryTextColor,
        iconText = stringResource(id = R.string.more),
        imageBackgroundColor = backgroundColor,
        addBorder = !playlist.isFavouritePlaylist
    )
}