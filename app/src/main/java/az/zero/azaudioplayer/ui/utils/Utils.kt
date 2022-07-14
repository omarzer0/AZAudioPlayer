package az.zero.azaudioplayer.ui.utils

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import az.zero.azaudioplayer.R
import az.zero.azaudioplayer.ui.theme.AZAudioPlayerTheme

fun setCompContent(context: Context,content: @Composable () -> Unit): View {
    return ComposeView(context).apply {
        setContent {
            AZAudioPlayerTheme {
//                ChangeStatusBarColor(
//                    statusColor = MaterialTheme.colors.primary,
//                    navigationBarColor = MaterialTheme.colors.primary
//                )
                content()
            }
        }
    }
}

fun createTimeLabel(time: Long): String {
    val min = time / 1000 / 60
    val sec = time / 1000 % 60
    var label = "$min:"
    if (sec < 10) label += "0"
    label += sec
    return label
}