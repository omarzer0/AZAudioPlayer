package az.zero.azaudioplayer.core

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun setFragmentContent(
        backgroundColor: Color? = null,
        content: @Composable () -> Unit
    ): View {
        return setCompContent(requireContext()) {
            val innerBackgroundColor = backgroundColor ?: MaterialTheme.colors.background
            Box(
                modifier = Modifier.background(innerBackgroundColor)
            ) {
                content()
            }
        }
    }

}