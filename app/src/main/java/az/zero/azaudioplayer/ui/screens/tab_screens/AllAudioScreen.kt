package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.composables.DropDownItemWithAction
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.composables.MenuActionType
import az.zero.azaudioplayer.ui.screens.tab_screens.MenuActionTypeForAllScreen.DELETE
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.db.entities.DBAudio

@Composable
fun AllAudioScreen(
    dbAudioList: List<DBAudio>?,
    selectedId: String,
    onAudioItemClick: (DBAudio) -> Unit,
    onAudioIconClick: (DBAudio, MenuActionType) -> Unit
) {
    if (dbAudioList.isNullOrEmpty()) return

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "${dbAudioList.size} ${stringResource(id = R.string.of_audios)}"
            ItemsHeader(text = headerText)
        }

        items(dbAudioList, key = { it.data }) { audio ->
            AudioItem(
                DBAudio = audio,
                menuItemList = menuActionList,
                isSelected = audio.data == selectedId,
                onClick = {
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
    DBAudio: DBAudio,
    isSelected: Boolean,
    annotatedTextQuery: String = "",
    onClick: () -> Unit,
    onIconClick: (MenuActionType) -> Unit,
    menuItemList: List<DropDownItemWithAction> = emptyList()

) {
    val textColor = if (isSelected) SelectedColor
    else MaterialTheme.colors.onPrimary

    BasicAudioItem(
        imageUrl = DBAudio.cover,
        cornerShape = CircleShape,
        topText = DBAudio.title,
        bottomTexts = listOf(DBAudio.artist, DBAudio.album),
        topTextColor = textColor,
        iconVector = Icons.Filled.MoreVert,
        iconColor = SecondaryTextColor,
        iconText = stringResource(id = R.string.more),
        onItemClick = onClick,
        onTailItemClick = onIconClick,
        annotatedTextQuery = annotatedTextQuery,
        menuItemList = menuItemList
    )
}
