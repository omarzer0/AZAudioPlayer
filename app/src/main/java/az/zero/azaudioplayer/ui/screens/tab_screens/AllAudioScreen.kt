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
import az.zero.azaudioplayer.ui.composables.DropDownItemWithAction
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.composables.MenuActionType
import az.zero.azaudioplayer.ui.screens.tab_screens.MenuActionTypeForAllScreen.DELETE
import az.zero.azaudioplayer.ui.screens.tab_screens.MenuActionTypeForAllScreen.EDIT
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor

@Composable
fun AllAudioScreen(
    audioList: List<Audio>?,
    selectedId: String,
    onAudioItemClick: (Audio) -> Unit,
    onAudioIconClick: (Audio, MenuActionType) -> Unit
) {

    if (audioList.isNullOrEmpty()) return

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "${audioList.size} ${stringResource(id = R.string.of_audios)}"
            ItemsHeader(text = headerText)
        }

        itemsIndexed(audioList) { index, audio ->
            AudioItem(
                audio = audio,
                menuItemList = menuActionList,
                isSelected = audio.data == selectedId,
                onClick = {
//                    selectedId = index
                    onAudioItemClick(audio)
                }, onIconClick = { menuAction ->
                    onAudioIconClick(audio, menuAction)
                })
        }
    }
}

val menuActionList = listOf(
    DropDownItemWithAction(R.string.delete, DELETE),
)

enum class MenuActionTypeForAllScreen : MenuActionType {
    DELETE,
    EDIT
}

@Composable
fun AudioItem(
    audio: Audio,
    isSelected: Boolean,
    annotatedTextQuery: String = "",
    onClick: () -> Unit,
    onIconClick: (MenuActionType) -> Unit,
    menuItemList: List<DropDownItemWithAction> = emptyList()

) {
    val textColor = if (isSelected) SelectedColor
    else MaterialTheme.colors.onPrimary

    BasicAudioItem(
        imageUrl = audio.cover,
        cornerShape = CircleShape,
        topText = audio.title,
        bottomTexts = listOf(audio.artist, audio.album),
        topTextColor = textColor,
        iconVector = Icons.Filled.MoreVert,
        iconColor = SecondaryTextColor,
        iconText = stringResource(id = R.string.more),
        onItemClick = { onClick() },
        onTailItemClick = onIconClick,
        annotatedTextQuery = annotatedTextQuery,
        menuItemList = menuItemList
    )
}
