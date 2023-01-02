package az.zero.azaudioplayer.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.player.extensions.EMPTY_AUDIO
import az.zero.azaudioplayer.ui.composables.*
import az.zero.azaudioplayer.ui.composables.ui_extensions.mirror
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.utils.LONG_TEXT
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            SearchScreen(viewModel = viewModel, navController = findNavController())
        }
    }
}

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavController,
) {

    var text by rememberSaveable {
        mutableStateOf("")
    }
    val allAudios = viewModel.allDBAudio.observeAsState().value ?: emptyList()
    val currentlyPlayingAudio = viewModel.currentPlayingAudio.observeAsState().value ?: EMPTY_AUDIO
    val selectedId = currentlyPlayingAudio.data

    SearchScreen(
        modifier = Modifier.background(MaterialTheme.colors.background),
        text = text,
        allAudios = allAudios,
        selectedId = selectedId,
        onSearch = {
            text = it
            viewModel.searchAudios(it)
        },
        onBackIconClick = { navController.navigateUp() },
        onClearClick = {
            text = ""
            viewModel.searchAudios("")
        },
        onAudioItemClick = { audio ->
            viewModel.audioAction(
                AudioActions.Toggle(audioDataId = audio.data),
                null
            )
        },
        onAudioIconClick = { dbAudio, menuActionType ->
            // TODO on audio more icon click impl
        },
    )
}

@Composable
private fun SearchScreen(
    modifier: Modifier = Modifier,
    allAudios: List<DBAudio>,
    selectedId: String,
    text: String,
    onSearch: (String) -> Unit,
    onBackIconClick: () -> Unit,
    onClearClick: () -> Unit,
    onAudioItemClick: (DBAudio) -> Unit,
    onAudioIconClick: (DBAudio, MenuActionType) -> Unit,

    ) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchFragmentHeader(
            text = text,
            hint = stringResource(id = R.string.search),
            onClearClick = onClearClick,
            onBackIconClick = onBackIconClick,
            onSearch = { query ->
                onSearch(query)
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            item {
//                val headerText =
//                    "${allAudios.size} ${stringResource(id = R.string.of_audios)}"
//                ItemsHeader(text = headerText)
//            }

            items(allAudios) { audio ->
                SearchAudioItem(
                    dbAudio = audio,
                    isSelected = audio.data == selectedId,
                    annotatedTextQuery = text,
                    onClick = {
                        onAudioItemClick(audio)
                    },
                    onIconClick = {
                        onAudioIconClick(audio, it)
                    })
            }
        }
    }
}

@Composable
fun SearchAudioItem(
    dbAudio: DBAudio,
    isSelected: Boolean,
    annotatedTextQuery: String,
    onClick: () -> Unit,
    onIconClick: (MenuActionType) -> Unit,
    menuItemList: List<DropDownItemWithAction> = emptyList(),
) {
    val textColor = if (isSelected) SelectedColor
    else MaterialTheme.colors.onPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSafeClick { onClick() }
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomImage(
            modifier = Modifier.size(48.dp),
            image = dbAudio.cover,
            cornerShape = CircleShape
        )

        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomTextWithAnnotatedText(
            modifier = Modifier.weight(1f),
            topTextString = dbAudio.title,
            bottomTextStrings = listOf(dbAudio.artist, dbAudio.album),
            topTextColor = textColor,
            annotatedTextQuery = annotatedTextQuery
        )

        Spacer(modifier = Modifier.width(16.dp))

        //TODO to be add to (ex: edit/delete) audio
//        IconWithMenu(
//            iconVector = Icons.Filled.MoreVert,
//            iconColor = SecondaryTextColor,
//            iconText = stringResource(id = R.string.more),
//            items = menuItemList,
//            onIconClick = onIconClick
//        )
    }
}

@Composable
fun SearchFragmentHeader(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "",
    onSearch: (String) -> Unit = {},
    onClearClick: () -> Unit,
    onBackIconClick: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(start = 4.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            IconButton(
                onClick = { onBackIconClick() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    stringResource(id = R.string.back),
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.mirror()
                )
            }

            TextWithClearIcon(
                modifier = Modifier.weight(1f),
                text = text,
                hint = hint,
                onTextValueChanged = {
                    onSearch(it)
                },
                onClearClick = { onClearClick() }
            )

        }

        Divider(
            modifier = Modifier.padding(top = 4.dp),
            color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
        )
    }
}
