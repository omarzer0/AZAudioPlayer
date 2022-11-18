package az.zero.azaudioplayer.ui.screens.sort.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.core.setCompContent
import az.zero.azaudioplayer.ui.composables.clickableSafeClick
import az.zero.azaudioplayer.ui.theme.SelectedColor
import az.zero.base.utils.AlbumSortBy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SortAlbumBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: SortAlbumBottomSheetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        dialog?.window?.apply {
            val layoutParams = attributes
            layoutParams.dimAmount = 0.4f
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = layoutParams
        }

        return setCompContent(requireContext()) {
            val sortAudioActions = getSortAudioActions()
            val sortBySingleValue = viewModel.sortAlbumBySingleValue
            val sortBy = viewModel.sortAlbumBy.observeAsState().value ?: sortBySingleValue

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.sort_by),
                    style = MaterialTheme.typography.h2,
                    color = MaterialTheme.colors.onBackground
                )
                Spacer(modifier = Modifier.size(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(sortAudioActions, key = { it.sortOrder }) {
                        SortAlbumItemBy(
                            selectedAudioSortBy = sortBy,
                            sortAudioAction = it,
                            onSelect = { newOrder ->
                                viewModel.onAlbumSortOrderChange(newOrder)
                                findNavController().navigateUp()
                            }
                        )
                    }
                }

            }
        }
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }
}

@Composable
fun SortAlbumItemBy(
    modifier: Modifier = Modifier,
    selectedAudioSortBy: AlbumSortBy,
    sortAudioAction: SortAlbumAction,
    onSelect: (AlbumSortBy) -> Unit,
) {
    Row(
        modifier = modifier
            .padding(start = 12.dp, bottom = 8.dp, top = 8.dp, end = 8.dp)
            .fillMaxWidth()
            .clickableSafeClick {
                onSelect(sortAudioAction.sortOrder)
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = sortAudioAction.text,
            style = MaterialTheme.typography.h2,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.size(16.dp))
        RadioButton(
            colors = RadioButtonDefaults.colors(selectedColor = SelectedColor),
            selected = selectedAudioSortBy == sortAudioAction.sortOrder,
            onClick = { onSelect(sortAudioAction.sortOrder) }
        )
    }
}

@Composable
fun getSortAudioActions(): List<SortAlbumAction> {
    return listOf(
        SortAlbumAction(sortOrder = AlbumSortBy.ALBUM_SORT_ASC,
            text = stringResource(id = R.string.sort_by_album_asc)),
        SortAlbumAction(sortOrder = AlbumSortBy.ALBUM_SORT_DESC,
            text = stringResource(id = R.string.sort_by_album_desc))
    )
}

data class SortAlbumAction(
    val sortOrder: AlbumSortBy,
    val text: String,
)


