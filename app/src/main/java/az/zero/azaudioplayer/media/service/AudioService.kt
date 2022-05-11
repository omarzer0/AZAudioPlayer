package az.zero.azaudioplayer.media.service

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import az.zero.azaudioplayer.media.audio_data_source.AudioDataSource
import az.zero.azaudioplayer.media.extensions.flag
import az.zero.azaudioplayer.media.extensions.toMediaSource
import az.zero.azaudioplayer.media.library.BROWSABLE_ROOT
import az.zero.azaudioplayer.media.library.BrowseTree
import az.zero.azaudioplayer.media.notifications.AudioNotificationManager
import az.zero.azaudioplayer.media.player.callbacks.PlaybackPreparer
import az.zero.azaudioplayer.media.player.callbacks.PlayerEventListener
import az.zero.azaudioplayer.media.player.callbacks.PlayerNotificationListener
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var audioDataSource: AudioDataSource

    private lateinit var audioNotificationManager: AudioNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false


    private lateinit var playerEventListener: PlayerEventListener

    //    private var isPlayerInitialized: Boolean = false
    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()
//    private var currentPlayingAudio: MediaMetadataCompat? = null

    private val browseTree: BrowseTree by lazy {
        BrowseTree(applicationContext, audioDataSource)
    }


    override fun onCreate() {
        super.onCreate()

        serviceScope.launch {
            audioDataSource.fetchMediaData()
        }

        audioDataSource.audiosLiveData.observeForever {
            Log.e("MainViewModel", "onCreate: ")
            preparePlaylist(null, it, exoPlayer.playWhenReady)
        }

//        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
//            PendingIntent.getActivity(
//                this,
//                0,
//                it,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        }

        mediaSessionCompat = MediaSessionCompat(this, "MusicService")
            .apply {
//                setSessionActivity(activityIntent)
                isActive = true
            }

        sessionToken = mediaSessionCompat.sessionToken

        audioNotificationManager = AudioNotificationManager(
            this,
            mediaSessionCompat.sessionToken,
            PlayerNotificationListener(this)
        ) {
            currentSongDuration = exoPlayer.duration
        }

        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat)
        mediaSessionConnector.setPlaybackPreparer(playbackPreparer)
        mediaSessionConnector.setQueueNavigator(AudioQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        playerEventListener = PlayerEventListener(this, audioNotificationManager, exoPlayer)
        exoPlayer.addListener(playerEventListener)
        audioNotificationManager.showNotification(exoPlayer)

    }

    inner class AudioQueueNavigator : TimelineQueueNavigator(mediaSessionCompat) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            // called when service needs a new description form a media item
            return audioDataSource.audios[windowIndex].description
        }
    }

    private val playbackPreparer by lazy {
        PlaybackPreparer(audioDataSource) { currentAudio, currentAudioPlayList, playWhenReady ->
            preparePlaylist(
                currentAudio,
                currentAudioPlayList,
                playWhenReady
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY


    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        Log.e("MainViewModel", "onGetRoot: ")
        return BrowserRoot(BROWSABLE_ROOT, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        Log.e("MainViewModel", "onLoadChildren: ")

        val resultsSent = audioDataSource.whenReady { successfullyInitialized ->
            if (successfullyInitialized) {
                Log.e("MainViewModel", "onLoadChildren: ")
                val children = browseTree[parentId]?.map { item ->
                    MediaBrowserCompat.MediaItem(item.description, item.flag)
                }
                result.sendResult(children?.toMutableList())
            } else {
                result.sendResult(null)
            }
        }

        if (!resultsSent) {
            result.detach()
        }
    }


    private fun preparePlaylist(
        itemToPlay: MediaMetadataCompat?,
        metadataList: List<MediaMetadataCompat>,
        playWhenReady: Boolean,
        playbackStartPositionMs: Long = 0L
    ) {
        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
        currentPlaylistItems = metadataList
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.stop(true)
        val mediaSource = metadataList.toMediaSource(dataSourceFactory)
        exoPlayer.prepare(mediaSource)
        exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs)
    }

    companion object {
        var currentSongDuration = 0L
            private set
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)

        exoPlayer.stop(/* reset= */true)
    }

    override fun onDestroy() {
        mediaSessionCompat.run {
            isActive = false
            release()
        }

        // Cancel coroutines when the service is going away.
        serviceJob.cancel()

        // Free ExoPlayer resources.
        exoPlayer.removeListener(playerEventListener)
        exoPlayer.release()
    }
}