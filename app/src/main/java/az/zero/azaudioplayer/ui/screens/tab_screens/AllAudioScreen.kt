package az.zero.azaudioplayer.ui.screens.tab_screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.composables.BasicAudioItem
import az.zero.azaudioplayer.ui.composables.DropDownItemWithAction
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.composables.MenuActionType
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.screens.tab_screens.MenuActionTypeForAllScreen.DELETE
import az.zero.azaudioplayer.ui.screens.tab_screens.MenuActionTypeForAllScreen.EDIT
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.utils.fakeAudio
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio


@Composable
fun AllAudioScreen(viewModel: HomeViewModel) {

    val allAudios = viewModel.allAudio.observeAsState().value ?: emptyList()
    val selectedId = viewModel.currentPlayingAudio.observeAsState().value?.data ?: ""
    AllAudioScreen(
        dbAudioList = allAudios,
        selectedId = selectedId,
        onAudioItemClick = { audio ->
            viewModel.audioAction(AudioActions.Toggle(audio.data), allAudios)
        },
        onAudioIconClick = { audio, menuAction ->
            when (menuAction) {
                DELETE -> {

                }
                EDIT -> {

                }
            }
        })
}

@Composable
private fun AllAudioScreen(
    dbAudioList: List<DBAudio>,
    selectedId: String,
    onAudioItemClick: (DBAudio) -> Unit,
    onAudioIconClick: (DBAudio, MenuActionType) -> Unit,
) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            val headerText = "${dbAudioList.size} ${stringResource(id = R.string.of_audios)}"
            ItemsHeader(text = headerText)
        }

        items(dbAudioList, key = { it.data }) { audio ->
            AudioItem(
                dbAudio = audio,
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
    dbAudio: DBAudio,
    isSelected: Boolean,
    annotatedTextQuery: String = "",
    onClick: () -> Unit,
    onIconClick: (MenuActionType) -> Unit,
    menuItemList: List<DropDownItemWithAction> = emptyList(),

    ) {
    val textColor = if (isSelected) SelectedColor
    else MaterialTheme.colors.onPrimary

    BasicAudioItem(
        imageUrl = dbAudio.cover,
        cornerShape = CircleShape,
        topText = dbAudio.title,
        bottomTexts = listOf(dbAudio.artist, dbAudio.album),
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
