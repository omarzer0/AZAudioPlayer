package az.zero.azaudioplayer.ui.composables

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import az.zero.azaudioplayer.R

@Composable
fun AppBarWithSearch(
    onSearchClick: () -> Unit,
    onMoreClick: () -> Unit,
    customDropDownContent: (@Composable () -> Unit)? = null,
) {
    BasicHeaderWithBackBtn(
        text = stringResource(id = R.string.app_name),
        actions = {
            customDropDownContent?.invoke()

            IconButton(onClick = { onSearchClick() }) {
                Icon(
                    Icons.Filled.Search,
                    stringResource(id = R.string.search),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            IconButton(onClick = { onMoreClick() }) {
                Icon(
                    Icons.Filled.MoreVert,
                    stringResource(id = R.string.more),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
    )
}