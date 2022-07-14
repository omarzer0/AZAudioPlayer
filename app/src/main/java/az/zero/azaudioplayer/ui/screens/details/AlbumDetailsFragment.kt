package az.zero.azaudioplayer.ui.screens.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.screens.home.HomeViewModel
import az.zero.azaudioplayer.ui.screens.tab_screens.AllAudioScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumDetailsFragment : BaseFragment() {
    private val viewModel: HomeViewModel by viewModels()
    private val args: AlbumDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val audioList = args.audioList.toList()
        return setFragmentContent {
            Surface(modifier = Modifier.background(MaterialTheme.colors.primary)) {
                AllAudioScreen(viewModel, audioList)
            }
        }
    }
}