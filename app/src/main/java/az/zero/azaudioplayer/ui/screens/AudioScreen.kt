package az.zero.azaudioplayer.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.distinctUntilChanged
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.db.entities.DBAudio
import az.zero.azaudioplayer.ui.MainViewModel
import az.zero.azaudioplayer.ui.common_composables.clickableSafeClick
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.ui_extensions.getColor
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph

@RootNavGraph(start = true)
@Destination
@Composable
fun AudioScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    var selected by remember { mutableStateOf(0) }

    val audioList by remember { viewModel.allAudio.distinctUntilChanged() }.observeAsState()

    LazyColumn {
        item {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }

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

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(audio.cover)
            .scale(Scale.FILL)
            .placeholder(R.drawable.ic_music)
            .error(R.drawable.ic_music)
            .build()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(width = 48.dp, height = 48.dp)
                .background(Color.White, CircleShape)
                .clip(CircleShape)
                .border(
                    border = BorderStroke(
                        width = 1.dp, brush = Brush.radialGradient(
                            colors = listOf(getColor("#FF6200EE"), getColor("#EE0014"))
                        )
                    ),
                    CircleShape
                ),
            alignment = Alignment.Center,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = audio.title,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = audio.artist, color = textColor, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            modifier = Modifier.weight(0.1f), onClick = {}) {
            Icon(
                Icons.Filled.MoreVert,
                stringResource(id = R.string.more),
                tint = textColor
            )
        }
    }

}