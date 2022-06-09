package az.zero.azaudioplayer.ui.utils

import android.content.Context
import android.view.View
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import az.zero.azaudioplayer.ui.theme.AZAudioPlayerTheme
import az.zero.azaudioplayer.ui.utils.common_composables.ChangeStatusBarColor

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