package az.zero.azaudioplayer.ui.screens.details.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.BasicHeaderWithBackBtn
import az.zero.azaudioplayer.ui.composables.BottomPlayer
import az.zero.azaudioplayer.ui.composables.MenuActionType
import az.zero.azaudioplayer.ui.composables.PlayAllHeader
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.tab_screens.AudioItem
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBArtistWithAudios
import az.zero.db.entities.DBAudio
import az.zero.player.extensions.EMPTY_AUDIO
import az.zero.player.extensions.isPlaying
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistDetailsFragment : BaseFragment() {
    private val args: ArtistDetailsFragmentArgs by navArgs()
    private val viewModel: ArtistDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        return setFragmentContent {
            val dbArtistWithAudioList = args.dbArtistWithAudios
            ArtistDetailsScreen(
                dbArtistWithAudioList = dbArtistWithAudioList,
                viewModel = viewModel,
                navController = findNavController()
            )
        }
    }
}

@Composable
fun ArtistDetailsScreen(
    dbArtistWithAudioList: DBArtistWithAudios,
    viewModel: ArtistDetailsViewModel,
    navController: NavController,
) {
    val currentlyPlayingAudio = viewModel.currentPlayingAudio.observeAsState().value ?: EMPTY_AUDIO
    val isPlaying = viewModel.playbackState.observeAsState().value?.isPlaying ?: false
    val enabled = currentlyPlayingAudio.data.isNotEmpty()

    ArtistDetailsScreen(
        currentlyPlayingAudio = currentlyPlayingAudio,
        isPlaying = isPlaying,
        enabled = enabled,
        playAllHeaderEnabled = dbArtistWithAudioList.dbAudioList.isNotEmpty(),
        onPlayAllClick = {
            viewModel.audioAction(
                action = AudioActions.PlayAll,
                newAudioList = dbArtistWithAudioList.dbAudioList
            )
        },
        onBackIconClick = { navController.navigateUp() },
        onBodyClick = { navController.navigate(HomeFragmentDirections.actionGlobalPlayerBottomSheetFragment()) },
        onPlayOrPauseClick = { viewModel.playOrPause() },
        onFavouriteClick = { viewModel.addOrRemoveFromFavourite(currentlyPlayingAudio) },
        dbArtistWithAudioList = dbArtistWithAudioList,
        onAudioClick = {
            viewModel.audioAction(
                AudioActions.Toggle(it.data),
                dbArtistWithAudioList.dbAudioList,
            )
        },
        onIconClick = { dbAudio, menuActionType ->

        },
    )
}


@Composable
fun ArtistDetailsScreen(
    modifier: Modifier = Modifier,
    currentlyPlayingAudio: DBAudio,
    isPlaying: Boolean,
    enabled: Boolean,
    playAllHeaderEnabled: Boolean = true,
    onPlayAllClick: () -> Unit,
    onBodyClick: () -> Unit,
    onPlayOrPauseClick: () -> Unit,
    onFavouriteClick: (Boolean) -> Unit,
    dbArtistWithAudioList: DBArtistWithAudios,
    onAudioClick: (DBAudio) -> Unit,
    onIconClick: (DBAudio, MenuActionType) -> Unit,
    onBackIconClick: () -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        BasicHeaderWithBackBtn(
            text = dbArtistWithAudioList.artist.name,
            onBackPressed = onBackIconClick
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                val headerText =
                    "${dbArtistWithAudioList.dbAudioList.size} ${stringResource(id = R.string.of_audios)}"
                PlayAllHeader(
                    text = headerText,
                    playAllHeaderEnabled = playAllHeaderEnabled,
                    onClick = onPlayAllClick
                )
            }

            items(dbArtistWithAudioList.dbAudioList, key = { it.data }) { audio ->
                AudioItem(
                    dbAudio = audio,
                    isSelected = audio.data == currentlyPlayingAudio.data,
                    onClick = { onAudioClick(audio) },
                    onIconClick = { onIconClick(audio, it) }
                )
            }
        }

        BottomPlayer(
            currentlyPlayingAudio = currentlyPlayingAudio,
            enabled = enabled,
            isPlaying = isPlaying,
            onBodyClick = onBodyClick,
            onFavouriteClick = onFavouriteClick,
            onPlayOrPauseClick = onPlayOrPauseClick
        )
    }
}