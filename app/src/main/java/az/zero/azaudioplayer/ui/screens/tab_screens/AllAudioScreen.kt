package az.zero.azaudioplayer.ui.screens.tab_screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale

@Composable
fun AllAudioScreen(viewModel: HomeViewModel, audioList: List<Audio>?, selected: Int = -1) {
    var selectedId = selected

    if (audioList.isNullOrEmpty()) return
    val listSize = audioList.size

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "$listSize ${stringResource(id = R.string.of_audios)}"
            ItemsHeader(text = headerText)
        }

        items(listSize) { index ->
            AudioItem(
                audioList[index],
                isSelected = selectedId == index
            ) {
                selectedId = index
                viewModel.play(audioList[index].data)
            }
        }
    }
}

@Composable
fun AudioItem(
    audio: Audio,
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
                        width = 0.5.dp, brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Red,
                                Color.Blue
                            )
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
                style = MaterialTheme.typography.h2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${audio.artist} - ${audio.album}",
                color = SecondaryTextColor,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            modifier = Modifier.weight(0.1f), onClick = {}) {
            Icon(
                Icons.Filled.MoreVert,
                stringResource(id = R.string.more),
                tint = SecondaryTextColor
            )
        }
    }

}