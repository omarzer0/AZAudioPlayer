package az.zero.azaudioplayer.ui

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import az.zero.azaudioplayer.db.AudioDao
import az.zero.azaudioplayer.media.connection.AudioServiceConnection
import az.zero.azaudioplayer.media.library.BROWSABLE_ROOT
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val audioServiceConnection: AudioServiceConnection,
    private val audioDao: AudioDao
) : ViewModel() {

    fun play(audioDataId: String) {
        audioServiceConnection.playPauseOrToggle(audioDataId)
    }

    init {
        audioServiceConnection.subscribe(BROWSABLE_ROOT,
            object : MediaBrowserCompat.SubscriptionCallback() {

            })
    }

    val rootMediaId: LiveData<String>
        get() = MutableLiveData(audioServiceConnection.rootMediaId)

    val x = ""

    val allAudio = audioDao.getAllDbAudio()

//    init {
//
//        val x = audioDao.getAllDbAudio()
//        x.observeForever {
//            Log.e("getAllDbAudio", "from vm$it")
//        }
//
//        audioDao.getAlbumWithAudio().observeForever {
//            Log.e("getAlbumWithAudio", "from vm$it")
//
//        }
//
//        audioServiceConnection.dataChanged.distinctUntilChanged().observeForever {
//            Log.e("MainViewModelOut", "changedddddddddddddddddddddddd")
//
//            audioServiceConnection.subscribe(
//                BROWSABLE_ROOT,
//                object : MediaBrowserCompat.SubscriptionCallback() {
//
//                    override fun onChildrenLoaded(
//                        parentId: String,
//                        children: MutableList<MediaBrowserCompat.MediaItem>
//                    ) {
//                        val itemsList = children.map { child ->
//                            val subtitle = child.description.subtitle ?: ""
//                            MediaItemData(
//                                child.mediaId!!,
//                                child.description.title.toString(),
//                                subtitle.toString(),
//                                child.description.iconUri!!,
//                                child.isBrowsable,
//                                R.drawable.ic_image
//                            )
//                        }
//                        Log.e("MainViewModelOut", "mmm${itemsList.size}|||$itemsList")
//
//                        audioServiceConnection.subscribe(
//                            itemsList[1].mediaId,
//                            object : MediaBrowserCompat.SubscriptionCallback() {
//                                override fun onChildrenLoaded(
//                                    parentId: String,
//                                    children: List<MediaBrowserCompat.MediaItem>
//                                ) {
//                                    val itemsList2 = children.map { child ->
//                                        val subtitle = child.description.subtitle ?: ""
//                                        MediaItemData(
//                                            child.mediaId!!,
//                                            child.description.title.toString(),
//                                            subtitle.toString(),
//                                            child.description.iconUri!!,
//                                            child.isBrowsable,
//                                            R.drawable.ic_image
//                                        )
//                                    }
//                                    Log.e("MainViewModelIn", "Let's see${itemsList2.size}")
//                                    Log.e("MainViewModelIn", "id:${itemsList2}")
//
//                                }
//                            })
//                    }
//                })
//
//        }
//    }
}