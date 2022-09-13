package az.zero.azaudioplayer.ui.composables

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R

@Composable
fun AppBar(
    onSearchClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colors.onPrimary
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 0.dp,
        actions = {
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