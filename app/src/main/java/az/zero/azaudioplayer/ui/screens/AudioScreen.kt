package az.zero.azaudioplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.db.entities.DBAudio
import az.zero.azaudioplayer.ui.MainViewModel
import az.zero.azaudioplayer.ui.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.theme.SelectedColor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true)
@Destination
@Composable
fun AudioScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    var selected by remember { mutableStateOf(0) }

    val audioList by viewModel.allAudio.observeAsState()

    LazyColumn {
        if (audioList.isNullOrEmpty()) return@LazyColumn
        val listSize = audioList?.size!!
        items(listSize) { index ->
            AudioItem(
                audioList!![index],
                isSelected = selected == index
            ) {
                selected = index
                viewModel.play(audioList!![index].data)
            }
        }
    }
}

@Composable
fun AudioItem(
    audio: DBAudio,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isSelected) SelectedColor
    else MaterialTheme.colors.onPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = audio.title,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = audio.displayName, color = textColor, fontSize = 14.sp)
        }
        IconButton(onClick = {}) {
            Icon(
                Icons.Filled.MoreVert,
                stringResource(id = R.string.more),
                tint = textColor
            )
        }
    }
}