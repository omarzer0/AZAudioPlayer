package az.zero.azaudioplayer.core

import android.view.View
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import az.zero.azaudioplayer.ui.theme.AZAudioPlayerTheme
import az.zero.azaudioplayer.ui.utils.common_composables.ChangeStatusBarColor
import az.zero.azaudioplayer.ui.utils.setCompContent

open class BaseFragment : Fragment() {

    fun setFragmentContent(content: @Composable () -> Unit): View {
        return setCompContent(requireContext()) {
            AZAudioPlayerTheme {
                ChangeStatusBarColor(
                    statusColor = MaterialTheme.colors.primary,
                    navigationBarColor = MaterialTheme.colors.primary
                )
                content()
            }
        }
    }

}