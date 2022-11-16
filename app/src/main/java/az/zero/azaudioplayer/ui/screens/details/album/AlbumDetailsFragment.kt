package az.zero.azaudioplayer.ui.screens.details.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.composables.CustomImage
import az.zero.azaudioplayer.ui.composables.MenuActionType
import az.zero.azaudioplayer.ui.composables.TopWithBottomText
import az.zero.azaudioplayer.ui.screens.tab_screens.AudioItem
import az.zero.azaudioplayer.ui.ui_utils.ui_extensions.mirror
import az.zero.azaudioplayer.utils.singleLineValue
import az.zero.base.utils.AudioActions
import az.zero.db.entities.DBAlbumWithAudioList
import az.zero.db.entities.DBAudio
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
    AlbumDetailsScreen(
        dbAlbumWithAudioList = dbAlbumWithAudioList,
        onAudioClick = {
            viewModel.audioAction(
                AudioActions.Toggle(it.data),
                dbAlbumWithAudioList.dbAudioList,
            )
        },
        onIconClick = { dbAudio, menuActionType -> },
        onBackIconClick = { navController.navigateUp() }
    )
}


@Composable
fun AlbumDetailsScreen(
    dbAlbumWithAudioList: DBAlbumWithAudioList,
    onAudioClick: (DBAudio) -> Unit,
    onIconClick: (DBAudio, MenuActionType) -> Unit,
    onBackIconClick: () -> Unit,
) {
    Column(
        modifier = Modifier.background(MaterialTheme.colors.background)
    ) {
        AlbumDetailsHeader(
            dbAlbumWithAudioList = dbAlbumWithAudioList,
            onBackIconClick = onBackIconClick
        )
        LazyColumn {
            item {
                AlbumDetailsSubHeader(dbAlbumWithAudioList = dbAlbumWithAudioList)
            }

            items(dbAlbumWithAudioList.dbAudioList, key = { it.data }) { audio ->
                AudioItem(
                    dbAudio = audio,
                    isSelected = false,
                    onClick = { onAudioClick(audio) },
                    onIconClick = { onIconClick(audio, it) }
                )
            }
        }
    }

}

@Composable
fun AlbumDetailsHeader(
    modifier: Modifier = Modifier,
    dbAlbumWithAudioList: DBAlbumWithAudioList,
    onBackIconClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = dbAlbumWithAudioList.album.name,
                color = MaterialTheme.colors.onPrimary,
                maxLines = singleLineValue,
                overflow = TextOverflow.Ellipsis
            )
        },
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        navigationIcon = {
            IconButton(onClick = { onBackIconClick() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    stringResource(id = R.string.back),
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.mirror()
                )
            }
        }
    )


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