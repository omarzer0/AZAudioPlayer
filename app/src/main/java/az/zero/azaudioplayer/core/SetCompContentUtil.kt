package az.zero.azaudioplayer.core

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import az.zero.azaudioplayer.ui.theme.AZAudioPlayerTheme


fun setCompContent(context: Context, content: @Composable () -> Unit): View {
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