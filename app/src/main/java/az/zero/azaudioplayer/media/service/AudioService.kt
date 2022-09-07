package az.zero.azaudioplayer.media.service

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import az.zero.azaudioplayer.domain.models.Audio
import az.zero.azaudioplayer.domain.models.toExoMediaItem
import az.zero.azaudioplayer.domain.models.toMediaItem
import az.zero.azaudioplayer.domain.models.toMediaMetadataCompat
import az.zero.azaudioplayer.media.audio_data_source.AudioDataSource
import az.zero.azaudioplayer.media.player.AudioNotificationManager
import az.zero.azaudioplayer.media.player.callbacks.PlayerEventListener
import az.zero.azaudioplayer.media.player.callbacks.PlayerNotificationListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AudioService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSource.Factory

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var audioDataSource: AudioDataSource

    private lateinit var audioNotificationManager: AudioNotificationManager
    private lateinit var playerEventListener: PlayerEventListener

    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    private var currentPlaylistItems: List<Audio> = emptyList()

    private var indexOfCurrentPlayingAudio = 0
    private var currentAudioPosition = 0L

    override fun onCreate() {
        super.onCreate()

        audioDataSource.audiosLiveData.observeForever { list ->
            if (list.isEmpty()) return@observeForever
            currentPlaylistItems = list
            val mediaItems = list.map { it.toExoMediaItem() }
            if (mediaItems.isEmpty()) return@observeForever
            indexOfCurrentPlayingAudio = exoPlayer.currentMediaItemIndex
            currentAudioPosition = exoPlayer.currentPosition
            exoPlayer.setMediaItems(mediaItems)
            exoPlayer.seekTo(indexOfCurrentPlayingAudio, currentAudioPosition)
        }

        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }


        mediaSessionCompat = MediaSessionCompat(this, "MusicService").apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        audioNotificationManager = AudioNotificationManager(
            this, mediaSessionCompat.sessionToken,
            PlayerNotificationListener(this)
        )

        sessionToken = mediaSessionCompat.sessionToken

        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat)
        mediaSessionConnector.setPlaybackPreparer(audioPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(AudioQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        playerEventListener = PlayerEventListener(this, audioNotificationManager, exoPlayer)
        exoPlayer.addListener(playerEventListener)

    }

    inner class AudioQueueNavigator : TimelineQueueNavigator(mediaSessionCompat) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            // called when service needs a new description form a media item
            return audioDataSource.audiosLiveData.value!![windowIndex].toMediaMetadataCompat().description
        }
    }

    private val audioPlaybackPreparer = object : MediaSessionConnector.PlaybackPreparer {
        override fun onCommand(
            player: Player,
            command: String,
            extras: Bundle?,
            cb: ResultReceiver?
        ): Boolean = false

        override fun getSupportedPrepareActions(): Long {
            return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                    PlaybackStateCompat.ACTION_SEEK_TO
        }

        override fun onPrepare(playWhenReady: Boolean) = Unit

        override fun onPrepareFromMediaId(
            mediaId: String,
            playWhenReady: Boolean,
            extras: Bundle?
        ) {
            val mediaIndex = currentPlaylistItems.indexOfFirst { it.data == mediaId }
            exoPlayer.playWhenReady = playWhenReady
            exoPlayer.seekTo(mediaIndex, 0)
            exoPlayer.prepare()
            audioNotificationManager.showNotification(exoPlayer)
        }

        override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) =
            Unit

        override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit

    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(BROWSABLE_ROOT, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        val resultsSent = audioDataSource.whenReady { successfullyInitialized ->
            if (successfullyInitialized) {
                val compatList = currentPlaylistItems.map { it.toMediaItem() }.toMutableList()
                result.sendResult(compatList)
            } else {
                result.sendResult(null)
            }
        }

        if (!resultsSent) {
            result.detach()
        }
    }


//    private fun preparePlaylist(
//        itemToPlay: MediaMetadataCompat?,
//        metadataList: List<MediaMetadataCompat>,
//        playWhenReady: Boolean,
//        playbackStartPositionMs: Long = 0L
//    ) {
//        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
//        currentPlaylistItems = metadataList.to
//        exoPlayer.playWhenReady = playWhenReady
//        exoPlayer.stop(true)
//        val mediaSource = metadataList.toMediaSource(dataSourceFactory)
//        exoPlayer.prepare(mediaSource)
//        exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs)
//        Log.e("playPauseOrToggle", "playPauseOrToggle:$playWhenReady")
//    }

    companion object {
        const val BROWSABLE_ROOT = "__ROOT__"
        private const val NOTIFICATION_ID = 1953
        const val CHANNEL_ID = "AZ player channel id"
        const val CHANNEL_NAME = "AZ player audio channel"
        const val DESCRIPTION = "AZ player Channel"
        const val NOTIFICATION_LIGHT_COLOR = Color.GREEN
//        private const val PLAYBACK_CHANNEL_ID = CHANNEL_ID
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaSessionCompat.apply {
            isActive = false
            release()
        }

        Log.e("onDestttt", "onDestroy: ")
        // Cancel coroutines when the service is going away.
//        serviceScope.cancel()
        // Free ExoPlayer resources.
        exoPlayer.release()
        isForegroundService = false
        stopForeground(true)
        stopSelf()
    }
}

//@AndroidEntryPoint
//class AudioService : MediaBrowserServiceCompat() {
//
//    @Inject
//    lateinit var dataSourceFactory: DefaultDataSourceFactory
//
//    @Inject
//    lateinit var exoPlayer: SimpleExoPlayer
//
//    @Inject
//    lateinit var audioDataSource: AudioDataSource
//
//    private lateinit var audioNotificationManager: AudioNotificationManager
//
//    private val serviceJob = Job()
//    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
//
//    private lateinit var mediaSessionCompat: MediaSessionCompat
//    private lateinit var mediaSessionConnector: MediaSessionConnector
//
//    var isForegroundService = false
//
//
//    private lateinit var playerEventListener: PlayerEventListener
//
//    //    private var isPlayerInitialized: Boolean = false
//    private var currentPlaylistItems: List<MediaMetadataCompat> = emptyList()
////    private var currentPlayingAudio: MediaMetadataCompat? = null
//
//    private val browseTree: BrowseTree by lazy {
//        // The browse tree listens and add audio from audio source but doesn't directly
//        // hand it to the player, Instead it build up the tree of mediaMetaDataCompat
//        // and when the service calls browseTree[parentId] it gets the data of the new
//        // list. BUT THE UI MUST ONLY SHOW THE SAME LIST AS IN THE PLAYER (DB LIST)
//        BrowseTree(applicationContext, audioDataSource)
//    }
//
//
//    override fun onCreate() {
//        super.onCreate()
//
//        serviceScope.launch {
//            audioDataSource.fetchMediaData()
//        }
//
//        audioDataSource.audiosLiveData.observeForever { dbAudioList ->
//            Log.e("MainViewModel", "onCreate: ")
//            val mediaMetaDataList = dbAudioList.map {
//                it.toMediaMetadataCompat()
//            }
//            preparePlaylist(null, mediaMetaDataList, exoPlayer.playWhenReady)
//        }
//
//        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
//            PendingIntent.getActivity(
//                this,
//                0,
//                it,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//        }
//
//        mediaSessionCompat = MediaSessionCompat(this, "MusicService")
//            .apply {
//                setSessionActivity(activityIntent)
//                isActive = true
//            }
//
//        sessionToken = mediaSessionCompat.sessionToken
//
//        audioNotificationManager = AudioNotificationManager(
//            this,
//            mediaSessionCompat.sessionToken,
//            PlayerNotificationListener(this)
//        ) {
//            currentSongDuration = exoPlayer.duration
//        }
//
//        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat)
////        mediaSessionConnector.setPlaybackPreparer(playbackPreparer)
//        mediaSessionConnector.setQueueNavigator(AudioQueueNavigator())
//        mediaSessionConnector.setPlayer(exoPlayer)
//
//        playerEventListener = PlayerEventListener(this, audioNotificationManager, exoPlayer)
//        exoPlayer.addListener(playerEventListener)
//        audioNotificationManager.showNotification(exoPlayer)
//
//    }
//
//    inner class AudioQueueNavigator : TimelineQueueNavigator(mediaSessionCompat) {
//        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
//            // called when service needs a new description form a media item
//            Log.e("playPauseOrToggle", "playPauseOrToggle: $windowIndex")
//
//            return audioDataSource.audiosLiveData.value!![windowIndex]
//                .toMediaMetadataCompat().description
//        }
//    }
//
//    private val playbackPreparer by lazy {
//        PlaybackPreparer(audioDataSource) { currentAudio, currentAudioPlayList, playWhenReady ->
//            preparePlaylist(
//                currentAudio,
//                currentAudioPlayList,
//                playWhenReady
//            )
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY
//
//
//    override fun onGetRoot(
//        clientPackageName: String,
//        clientUid: Int,
//        rootHints: Bundle?
//    ): BrowserRoot {
//        Log.e("MainViewModel", "onGetRoot: ")
//        return BrowserRoot(BROWSABLE_ROOT, null)
//    }
//
//    override fun onLoadChildren(
//        parentId: String,
//        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
//    ) {
//        Log.e("MainViewModel", "onLoadChildren: ")
//
//        val resultsSent = audioDataSource.whenReady { successfullyInitialized ->
//            if (successfullyInitialized) {
//                Log.e("MainViewModel", "onLoadChildren: ")
//                val children = browseTree[parentId]?.map { item ->
//                    MediaBrowserCompat.MediaItem(item.description, item.flag)
//                }
//                result.sendResult(children?.toMutableList())
//            } else {
//                result.sendResult(null)
//            }
//        }
//
//        if (!resultsSent) {
//            result.detach()
//        }
//    }
//
//
//    private fun preparePlaylist(
//        itemToPlay: MediaMetadataCompat?,
//        metadataList: List<MediaMetadataCompat>,
//        playWhenReady: Boolean,
//        playbackStartPositionMs: Long = 0L
//    ) {
//        val initialWindowIndex = if (itemToPlay == null) 0 else metadataList.indexOf(itemToPlay)
//        currentPlaylistItems = metadataList
//        exoPlayer.playWhenReady = playWhenReady
//        exoPlayer.stop(true)
//        val mediaSource = metadataList.toMediaSource(dataSourceFactory)
//        exoPlayer.prepare(mediaSource)
//        exoPlayer.seekTo(initialWindowIndex, playbackStartPositionMs)
//        Log.e("playPauseOrToggle", "playPauseOrToggle:$playWhenReady")
//    }
//
//    companion object {
//        var currentSongDuration = 0L
//            private set
//    }
//
//    override fun onTaskRemoved(rootIntent: Intent) {
//        super.onTaskRemoved(rootIntent)
//
//        exoPlayer.stop(/* reset= */true)
//    }
//
//    override fun onDestroy() {
//        mediaSessionCompat.run {
//            isActive = false
//            release()
//        }
//
//        // Cancel coroutines when the service is going away.
//        serviceJob.cancel()
//
//        // Free ExoPlayer resources.
//        exoPlayer.removeListener(playerEventListener)
//        exoPlayer.release()
//    }
//}