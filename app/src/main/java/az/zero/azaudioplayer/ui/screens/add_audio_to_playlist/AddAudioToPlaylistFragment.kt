package az.zero.azaudioplayer.ui.screens.add_audio_to_playlist

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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import az.zero.azaudioplayer.data.local.model.audio.SelectableAudio
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.TextWithClearIcon
import az.zero.azaudioplayer.ui.composables.TopWithBottomTextWithAnnotatedText
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.composables.ui_extensions.mirror
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAudioToPlaylistFragment : BaseFragment() {

    private val viewModel: AddAudioToPlaylistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            AddAudioToPlaylistScreen(viewModel = viewModel, navController = findNavController())
        }
    }
}

@Composable
fun AddAudioToPlaylistScreen(
    viewModel: AddAudioToPlaylistViewModel,
    navController: NavController,
) {
    val audioList = viewModel.allDBAudio.observeAsState().value ?: emptyList()
    val selectedId = viewModel.currentPlayingAudio.observeAsState().value?.data ?: ""

    var text by rememberSaveable { mutableStateOf("") }

    AddAudioToPlaylistScreen(
        modifier = Modifier.background(MaterialTheme.colors.background),
        audioList = audioList,
        selectedId = selectedId,
        text = text,
        onBackIconClick = { navController.navigateUp() },
        onSearch = {
            text = it
            viewModel.searchAudios(it)
        },
        onClearClick = {
            text = ""
            viewModel.searchAudios("")
        },
        onDone = {
            viewModel.onDone()
            navController.navigateUp()
        },
        onAudioSelectionChange = {
            viewModel.addOrRemoveAudio(it)
        }
    )
}

@Composable
fun AddAudioToPlaylistScreen(
    modifier: Modifier = Modifier,
    audioList: List<SelectableAudio>,
    selectedId: String,
    text: String,
    onBackIconClick: () -> Unit,
    onSearch: (query: String) -> Unit,
    onClearClick: () -> Unit,
    onDone: () -> Unit,
    onAudioSelectionChange: (audioId: String) -> Unit,
) {

    Column(modifier = modifier.fillMaxSize()) {

        AddAudioToPlaylistHeader(
            hint = stringResource(id = R.string.search),
            text = text,
            onClearClick = onClearClick,
            onDone = onDone,
            onBackIconClick = onBackIconClick,
            onTextValueChanged = { query ->
                onSearch(query)
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            item {
//                val headerText = "${audioList.size} ${stringResource(id = R.string.of_audios)}"
//                ItemsHeader(text = headerText)
//            }

            items(items = audioList, key = { it.audio.data }) { audioWithSelected ->
                SelectableAudiItem(
                    selectableAudio = audioWithSelected,
                    onAudioSelectionChange = onAudioSelectionChange,
                    currentlyPlaying = audioWithSelected.audio.data == selectedId
                )
            }

        }
    }
}

@Composable
fun AddAudioToPlaylistHeader(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "",
    onTextValueChanged: (String) -> Unit = {},
    onClearClick: () -> Unit,
    onBackIconClick: () -> Unit,
    onDone: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TextFieldDefaults.MinHeight)
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
                onTextValueChanged = { onTextValueChanged(it) },
                onClearClick = { onClearClick() }
            )

            IconButton(onClick = { onDone() }) {
                Icon(
                    Icons.Filled.Check,
                    stringResource(id = R.string.done),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }

        Divider(
            modifier = Modifier.padding(top = 4.dp),
            color = if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray,
        )
    }
}


@Composable
fun SelectableAudiItem(
    selectableAudio: SelectableAudio,
    onAudioSelectionChange: (audioId: String) -> Unit,
    currentlyPlaying: Boolean,
) {
    val textColor = if (currentlyPlaying) SelectedColor
    else MaterialTheme.colors.onPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 8.dp),
    ) {
        CustomImage(
            image = selectableAudio.audio.cover,
            modifier = Modifier.size(48.dp),
            cornerShape = CircleShape
        )
        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomTextWithAnnotatedText(
            modifier = Modifier.weight(1f),
            topTextString = selectableAudio.audio.displayName,
            topTextColor = textColor,
            bottomTextStrings = listOf(selectableAudio.audio.artist, selectableAudio.audio.album)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Checkbox(
            checked = selectableAudio.selected,
            onCheckedChange = {
                onAudioSelectionChange(selectableAudio.audio.data)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = SelectedColor,
                checkmarkColor = Color.White
            ),
        )
    }
}

//@Composable
//fun AddAudioToPlaylistHeader(
//    onBackIconClick: () -> Unit,
//    onDone: () -> Unit
//) {
//    TopAppBar(
//        title = {
//            Text(
//                text = stringResource(id = R.string.add_audio),
//                color = MaterialTheme.colors.onPrimary
//            )
//        },
//        backgroundColor = MaterialTheme.colors.primary,
//        elevation = 0.dp,
//        navigationIcon = {
//            IconButton(
//                onClick = { onBackIconClick() }) {
//                Icon(
//                    Icons.Filled.ArrowBack,
//                    stringResource(id = R.string.back),
//                    tint = MaterialTheme.colors.onPrimary,
//                    modifier = Modifier.mirror()
//                )
//            }
//        },
//        actions = {
//            IconButton(onClick = { onDone() }) {
//                Icon(
//                    Icons.Filled.Check,
//                    stringResource(id = R.string.done),
//                    tint = MaterialTheme.colors.onPrimary
//                )
//            }
//        }
//    )
//}
