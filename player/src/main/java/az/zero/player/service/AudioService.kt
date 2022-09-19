package az.zero.player.service

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
import az.zero.azaudioplayer.media.player.extensions.NOTHING_DESCRIPTION
import az.zero.db.entities.DBAudio
import az.zero.player.audio_data_source.AudioDataSource
import az.zero.player.callbacks.AudioNotificationManager
import az.zero.player.callbacks.PlayerEventListener
import az.zero.player.callbacks.PlayerNotificationListener
import az.zero.player.utils.toExoMediaItem
import az.zero.player.utils.toMediaItem
import az.zero.player.utils.toMediaMetadataCompat
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

    private var currentPlaylistItems: List<DBAudio> = emptyList()

//    private var indexOfCurrentPlayingAudio = 0
//    private var currentAudioPosition = 0L

    override fun onCreate() {
        super.onCreate()

        Log.e("AudioService", "onCreate: ")

        audioDataSource.audiosLiveData.observeForever { list ->
            if (list.isEmpty()) return@observeForever
            currentPlaylistItems = list
            val mediaItems = list.map { it.toExoMediaItem() }
            if (mediaItems.isEmpty()) return@observeForever
            exoPlayer.setMediaItems(mediaItems)
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

        Log.e("pendingIntent", "${audioDataSource.pendingIntent}" )
        audioNotificationManager = AudioNotificationManager(
            this, mediaSessionCompat.sessionToken,
            PlayerNotificationListener(this),
            audioDataSource.pendingIntent
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
            return audioDataSource.audiosLiveData.value?.get(windowIndex)
                ?.toMediaMetadataCompat()?.description ?: NOTHING_DESCRIPTION
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
            if (currentPlaylistItems.isEmpty()) return
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