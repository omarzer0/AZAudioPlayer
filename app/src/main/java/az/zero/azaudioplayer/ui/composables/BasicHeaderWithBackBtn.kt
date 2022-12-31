package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.composables.ui_extensions.mirror
import az.zero.azaudioplayer.utils.LONG_TEXT
import az.zero.azaudioplayer.utils.singleLineValue

@Composable
fun BasicHeaderWithBackBtn(
    modifier: Modifier = Modifier,
    text: String,
    onBackPressed: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(
                text = text,
                color = MaterialTheme.colors.onPrimary,
                maxLines = singleLineValue,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = actions,
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        navigationIcon = if (onBackPressed == null) null
        else {
            {
                IconButton(
                    onClick = { onBackPressed.invoke() }) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        stringResource(id = R.string.back),
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier.mirror()
                    )
                }
            }
        }

    )
}