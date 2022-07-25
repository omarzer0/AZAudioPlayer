package az.zero.azaudioplayer.ui.screens.new_playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import az.zero.azaudioplayer.core.BaseFragment
import az.zero.azaudioplayer.ui.screens.home.HomeFragmentContent
import az.zero.azaudioplayer.ui.screens.home.getTabsName
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewPlaylistFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return setFragmentContent {

        }
    }
}