package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.utils.common_composables.clickableSafeClick

@Composable
fun AllAudioScreen(viewModel: HomeViewModel, audioList: List<Audio>?, selected: Int = -1) {
    var selectedId = selected

    if (audioList.isNullOrEmpty()) return

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "${audioList.size} ${stringResource(id = R.string.of_audios)}"
            ItemsHeader(text = headerText)
        }

        itemsIndexed(audioList) { index, audio ->
            AudioItem(
                audio,
                isSelected = selectedId == index
            ) {
                selectedId = index
                viewModel.play(audio.data)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CustomImage(image = audio.cover, cornerShape = CircleShape)

        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomText(
            modifier = Modifier.weight(0.6f),
            topTextName = audio.title,
            bottomTextName = "${audio.artist} - ${audio.album}",
            topTextColor = textColor
        )

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