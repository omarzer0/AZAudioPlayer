package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.screens.home.AudioActions
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor

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
                isSelected = selectedId == index,
                onClick = {
                    selectedId = index
                    viewModel.audioAction(AudioActions.Toggle(audio.data))
                }, onIconClick = {

                })
        }
    }
}

@Composable
fun AudioItem(
    audio: Audio,
    isSelected: Boolean,
    onClick: () -> Unit,
    onIconClick: () -> Unit
) {
    val textColor = if (isSelected) SelectedColor
    else MaterialTheme.colors.onPrimary

    BasicAudioItem(
        imageUrl = audio.cover,
        cornerShape = CircleShape,
        topText = audio.title,
        bottomText = "${audio.artist} - ${audio.album}",
        topTextColor = textColor,
        iconVector = Icons.Filled.MoreVert,
        iconColor = SecondaryTextColor,
        iconText = stringResource(id = R.string.more),
        onItemClick = { onClick() },
        onTailItemClick = onIconClick
    )
}
