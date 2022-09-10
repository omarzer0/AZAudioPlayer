package az.zero.azaudioplayer.ui.screens.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.screens.tab_screens.AllAudioScreen
import az.zero.base.utils.AudioActions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlbumDetailsFragment : BaseFragment() {
    private val args: AlbumDetailsFragmentArgs by navArgs()
    private val viewModel: AlbumDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val audioList = args.audioList.toList()
        return setFragmentContent {
            AllAudioScreen(audioList,
                "",
                onAudioItemClick = { audio ->
                    viewModel.audioAction(AudioActions.Toggle(audio.data))
                }, onAudioIconClick = { audio, menuActionType ->

                })
        }
    }
}