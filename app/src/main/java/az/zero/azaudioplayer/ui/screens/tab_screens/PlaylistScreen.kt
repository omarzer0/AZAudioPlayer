package az.zero.azaudioplayer.ui.screens.tab_screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel

@Composable
fun PlaylistScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {

    val allPlaylist = viewModel.allPlaylists.observeAsState().value
    if (allPlaylist.isNullOrEmpty()) return
    val listSize = allPlaylist.size

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(listSize) { index ->
            Text(text = allPlaylist[index].name + "   ${allPlaylist[index].audioList.size}")
        }

    }

}