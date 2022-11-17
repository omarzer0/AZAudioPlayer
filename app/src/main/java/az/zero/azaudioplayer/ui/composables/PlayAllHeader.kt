package az.zero.azaudioplayer.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import az.zero.azaudioplayer.ui.theme.SecondaryTextColor

@Composable
fun PlayAllHeader(
    text: String,
    bottomDividerVisible: Boolean = false,
    content: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, top = 8.dp)
    ) {
        Row(
            modifier = Modifier
        ) {
            if (content != null) {
                content()
                Spacer(modifier = Modifier.width(160.dp))
            }
            Text(
                text = text, color = SecondaryTextColor,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        if (bottomDividerVisible) {
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(Color.Gray)
            )
        }
    }
}
