package az.zero.azaudioplayer.ui.screens.details.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.BasicHeaderWithBackBtn
import az.zero.azaudioplayer.ui.composables.MenuActionType
import az.zero.azaudioplayer.ui.screens.tab_screens.AudioItem
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBArtistWithAudios
import az.zero.db.entities.DBAudio
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
    ArtistDetailsScreen(
        dbArtistWithAudioList = dbArtistWithAudioList,
        onAudioClick = {
            viewModel.audioAction(
                AudioActions.Toggle(it.data),
                dbArtistWithAudioList.dbAudioList,
            )
        },
        onIconClick = { dbAudio, menuActionType -> },
        onBackIconClick = { navController.navigateUp() }
    )
}


@Composable
fun ArtistDetailsScreen(
    dbArtistWithAudioList: DBArtistWithAudios,
    onAudioClick: (DBAudio) -> Unit,
    onIconClick: (DBAudio, MenuActionType) -> Unit,
    onBackIconClick: () -> Unit,
) {

    Column(
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {
        BasicHeaderWithBackBtn(
            text = dbArtistWithAudioList.artist.name,
            onBackPressed = onBackIconClick
        )

        LazyColumn {
            items(dbArtistWithAudioList.dbAudioList, key = { it.data }) { audio ->
                AudioItem(dbAudio = audio,
                    isSelected = false,
                    onClick = { onAudioClick(audio) },
                    onIconClick = { onIconClick(audio, it) }
                )
            }
        }
    }
}