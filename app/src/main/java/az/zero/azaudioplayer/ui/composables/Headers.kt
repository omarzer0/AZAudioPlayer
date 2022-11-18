package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor
import az.zero.azaudioplayer.ui.theme.SelectedColor

@Composable
fun PlayAllHeader(
    modifier: Modifier = Modifier,
    text: String,
    playAllHeaderEnabled: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickableSafeClick(enabled = playAllHeaderEnabled, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            modifier = Modifier
                .size(25.dp),
            imageVector = Icons.Filled.PlayCircle,
            tint = SelectedColor,
            contentDescription = stringResource(id = R.string.play_all)
        )
        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = stringResource(id = R.string.play_all),
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h2,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = text, color = SecondaryTextColor,
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
fun TextHeader(
    modifier: Modifier = Modifier,
    text: String,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = text, color = SecondaryTextColor,
            style = MaterialTheme.typography.body1,
        )
    }
}