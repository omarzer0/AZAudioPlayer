package az.zero.azaudioplayer.ui.screens.details.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.*
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentDirections
import az.zero.azaudioplayer.ui.screens.tab_screens.AudioItem
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAlbumWithAudioList
import az.zero.db.entities.DBAudio
import az.zero.player.extensions.EMPTY_AUDIO
import az.zero.player.extensions.isPlaying
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumDetailsFragment : BaseFragment() {
    private val args: AlbumDetailsFragmentArgs by navArgs()
    private val viewModel: AlbumDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        val dbAlbumWithAudioList = args.dbAlbumWithAudioList
        return setFragmentContent {
            AlbumDetailsScreen(
                dbAlbumWithAudioList = dbAlbumWithAudioList,
                viewModel = viewModel,
                navController = findNavController()
            )
        }
    }
}

@Composable
fun AlbumDetailsScreen(
    dbAlbumWithAudioList: DBAlbumWithAudioList,
    viewModel: AlbumDetailsViewModel,
    navController: NavController,
) {
    val currentlyPlayingAudio = viewModel.currentPlayingAudio.observeAsState().value ?: EMPTY_AUDIO
    val isPlaying = viewModel.playbackState.observeAsState().value?.isPlaying ?: false
    val enabled = currentlyPlayingAudio.data.isNotEmpty()

    AlbumDetailsScreen(
        currentlyPlayingAudio = currentlyPlayingAudio,
        isPlaying = isPlaying,
        enabled = enabled,
        dbAlbumWithAudioList = dbAlbumWithAudioList,
        playAllHeaderEnabled = dbAlbumWithAudioList.dbAudioList.isNotEmpty(),
        onPlayAllClick = {
            viewModel.audioAction(
                action = AudioActions.PlayAll,
                newAudioList = dbAlbumWithAudioList.dbAudioList
            )
        },
        onBackIconClick = { navController.navigateUp() },
        onBodyClick = { navController.navigate(HomeFragmentDirections.actionGlobalPlayerBottomSheetFragment()) },
        onPlayOrPauseClick = { viewModel.playOrPause() },
        onFavouriteClick = { viewModel.addOrRemoveFromFavourite(currentlyPlayingAudio) },
        onAudioClick = {
            viewModel.audioAction(
                AudioActions.Toggle(it.data),
                dbAlbumWithAudioList.dbAudioList,
            )
        },
        onIconClick = { dbAudio, menuActionType ->

        },
    )
}


@Composable
fun AlbumDetailsScreen(
    modifier: Modifier = Modifier,
    currentlyPlayingAudio: DBAudio,
    isPlaying: Boolean,
    enabled: Boolean,
    playAllHeaderEnabled: Boolean = true,
    onPlayAllClick: () -> Unit,
    onBodyClick: () -> Unit,
    onPlayOrPauseClick: () -> Unit,
    onFavouriteClick: (Boolean) -> Unit,
    dbAlbumWithAudioList: DBAlbumWithAudioList,
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
            text = "",
            onBackPressed = onBackIconClick
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {

            item {
                AlbumDetailsSubHeader(dbAlbumWithAudioList = dbAlbumWithAudioList)
            }

            item {
                val headerText =
                    "${dbAlbumWithAudioList.dbAudioList.size} ${stringResource(id = R.string.of_audios)}"
                PlayAllHeader(
                    text = headerText,
                    playAllHeaderEnabled = playAllHeaderEnabled,
                    onClick = onPlayAllClick
                )
            }

            items(dbAlbumWithAudioList.dbAudioList, key = { it.data }) { audio ->
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

@Composable
fun AlbumDetailsSubHeader(
    dbAlbumWithAudioList: DBAlbumWithAudioList,
) {
    val image = if (dbAlbumWithAudioList.dbAudioList.isEmpty()) ""
    else dbAlbumWithAudioList.dbAudioList[0].cover

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        CustomImage(
            modifier = Modifier.size(48.dp),
            image = image
        )
        Spacer(
            modifier = Modifier.width(16.dp)
        )
        TopWithBottomText(
            topTextString = dbAlbumWithAudioList.album.name,
            bottomTextString = "${dbAlbumWithAudioList.dbAudioList.size} ${stringResource(id = R.string.audios)}"
        )
    }
}