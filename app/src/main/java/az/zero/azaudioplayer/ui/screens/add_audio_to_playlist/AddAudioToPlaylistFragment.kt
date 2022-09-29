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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.composables.TextWithClearIcon
import az.zero.azaudioplayer.ui.composables.TopWithBottomTextWithAnnotatedText
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.azaudioplayer.ui.ui_utils.ui_extensions.mirror
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAudioToPlaylistFragment : BaseFragment() {

    private val viewModel: AddAudioToPlaylistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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


    AddAudioToPlaylistScreen(
        audioList = audioList,
        selectedId = selectedId,
        onBackIconClick = { navController.navigateUp() },
        onSearch = { viewModel.searchAudios(it) },
        onClearClick = { viewModel.searchAudios("") },
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
    audioList: List<AudioWithSelected>,
    selectedId: String,
    onBackIconClick: () -> Unit,
    onSearch: (query: String) -> Unit,
    onClearClick: () -> Unit,
    onDone: () -> Unit,
    onAudioSelectionChange: (audioId: String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        AddAudioToPlaylistHeader(
            onBackIconClick = onBackIconClick,
            onDone = onDone
        )

        var text by rememberSaveable {
            mutableStateOf("")
        }

        Spacer(modifier = Modifier.size(16.dp))

        CustomSearchBar(
            text = text,
            hint = stringResource(id = R.string.search),
            onSearch = { query ->
                text = query
                onSearch(query)
            },
            onClearClick = {
                onClearClick()
                text = ""
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                val headerText =
                    "${audioList.size} ${stringResource(id = R.string.of_audios)}"
                ItemsHeader(text = headerText)
            }

            items(items = audioList, key = { it.audio.data }) { audioWithSelected ->
                SelectableAudiItem(
                    audioWithSelected = audioWithSelected,
                    onAudioSelectionChange = onAudioSelectionChange,
                    currentlyPlaying = audioWithSelected.audio.data == selectedId
                )
            }

        }
    }
}

@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    text: String,
    hint: String = "",
    onSearch: (String) -> Unit = {},
    onClearClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 8.dp, end = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSystemInDarkTheme()) Color.DarkGray else Color.LightGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = 8.dp),
            imageVector = Icons.Filled.Search,
            contentDescription = ""
        )
        Spacer(modifier = Modifier.width(16.dp))

        var isHintDisplayed by remember {
            mutableStateOf(hint != "")
        }

        TextWithClearIcon(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            onShouldShowHint = { isHintDisplayed = it },
            isClearIconVisible = !isHintDisplayed,
            onSearch = { onSearch(it) },
            onClearClick = { onClearClick() }
        )

    }
}

@Composable
fun SelectableAudiItem(
    audioWithSelected: AudioWithSelected,
    onAudioSelectionChange: (audioId: String) -> Unit,
    currentlyPlaying: Boolean
) {
    val textColor = if (currentlyPlaying) SelectedColor
    else MaterialTheme.colors.onPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 8.dp),
    ) {
        CustomImage(
            image = audioWithSelected.audio.cover,
            modifier = Modifier.size(48.dp),
            cornerShape = CircleShape
        )
        Spacer(modifier = Modifier.width(16.dp))

        TopWithBottomTextWithAnnotatedText(
            modifier = Modifier.weight(1f),
            topTextName = audioWithSelected.audio.displayName,
            topTextColor = textColor,
            bottomTextNames = listOf(audioWithSelected.audio.artist, audioWithSelected.audio.album)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Checkbox(
            checked = audioWithSelected.selected,
            onCheckedChange = {
                onAudioSelectionChange(audioWithSelected.audio.data)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = SelectedColor,
            ),
        )
    }
}

@Composable
fun AddAudioToPlaylistHeader(
    onBackIconClick: () -> Unit,
    onDone: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.add_audio),
                color = MaterialTheme.colors.onPrimary
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        navigationIcon = {
            IconButton(
                onClick = { onBackIconClick() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    stringResource(id = R.string.back),
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.mirror()
                )
            }
        },
        actions = {
            IconButton(onClick = { onDone() }) {
                Icon(
                    Icons.Filled.Check,
                    stringResource(id = R.string.done),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
}
