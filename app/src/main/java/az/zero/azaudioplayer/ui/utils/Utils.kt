package az.zero.azaudioplayer.ui.utils

import android.content.Context
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

fun setCompContent(context: Context,content: @Composable () -> Unit): View {
    return ComposeView(context).apply {
        setContent {
            content()
        }
    }
}