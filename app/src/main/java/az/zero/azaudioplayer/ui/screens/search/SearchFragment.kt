package az.zero.azaudioplayer.ui.screens.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.ItemsHeader
import az.zero.azaudioplayer.ui.composables.MenuActionType
import az.zero.azaudioplayer.ui.composables.SearchBar
import az.zero.azaudioplayer.ui.screens.tab_screens.AudioItem
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAudio
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return setFragmentContent {
            SearchScreen(viewModel = viewModel, navController = findNavController())
        }
    }
}

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    navController: NavController
) {

    SearchScreen(
        allAudios = viewModel.allDBAudio.observeAsState().value ?: emptyList(),
        selectedId = viewModel.currentPlayingAudio.observeAsState().value?.data ?: "",
        onSearch = { viewModel.searchAudios(it) },
        onBackBtnClick = { navController.navigateUp() },
        onClearClick = { viewModel.searchAudios("") },
        onAudioItemClick = { audio ->
            viewModel.audioAction(
                AudioActions.Toggle(audioDataId = audio.data),
                null
            )
        },
        onAudioIconClick = { dbAudio, menuActionType ->
            // TODO on audio more icon click impl
        }
    )
}

@Composable
private fun SearchScreen(
    allAudios: List<DBAudio>,
    selectedId: String,
    onSearch: (String) -> Unit,
    onBackBtnClick: () -> Unit,
    onClearClick: () -> Unit,
    onAudioItemClick: (DBAudio) -> Unit,
    onAudioIconClick: (DBAudio, MenuActionType) -> Unit

) {
    var text by rememberSaveable {
        mutableStateOf("")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            hint = stringResource(id = R.string.search),
            onSearch = { query ->
                text = query
                onSearch(query)
            }, onBackBtnClick = onBackBtnClick, onClearClick = {
                text = ""
                onClearClick()
            })

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                val headerText =
                    "${allAudios.size} ${stringResource(id = R.string.of_audios)}"
                ItemsHeader(text = headerText)
            }

            items(allAudios) { audio ->
                AudioItem(
                    audio,
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

