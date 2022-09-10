//package az.zero.azaudioplayer.media.player.callbacks
//
//import android.net.Uri
//import android.os.Bundle
//import android.os.ResultReceiver
//import android.support.v4.media.MediaMetadataCompat
//import android.support.v4.media.session.PlaybackStateCompat
//import az.zero.azaudioplayer.db.entities.toMediaMetadataCompat
//import az.zero.player.audio_data_source.AudioDataSource
//import az.zero.azaudioplayer.media.extensions.album
//import az.zero.azaudioplayer.media.extensions.id
//import az.zero.azaudioplayer.media.extensions.trackNumber
//import com.google.android.exoplayer2.Player
//import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
//
//class PlaybackPreparer(
//    private val audioDataSource: AudioDataSource,
//    private val playerPrepared: (
//        currentAudio: MediaMetadataCompat?,
//        currentAudioPlayList: List<MediaMetadataCompat>,
//        playWhenReady: Boolean
//    ) -> Unit
//) : MediaSessionConnector.PlaybackPreparer {
//
//    override fun onCommand(
//        player: Player,
//        command: String,
//        extras: Bundle?,
//        cb: ResultReceiver?
//    ): Boolean = false
//
//    override fun getSupportedPrepareActions(): Long {
//        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or
//                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
//    }
//
//    override fun onPrepare(playWhenReady: Boolean) = Unit
//
//    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
//        audioDataSource.whenReady {
//            val itemToPlay: MediaMetadataCompat? =
//                audioDataSource.audiosLiveData.value?.find { item ->
//                    item.toMediaMetadataCompat().id == mediaId
//                }?.toMediaMetadataCompat()
//            if (itemToPlay != null) {
//                playerPrepared(
//                    itemToPlay,
//                    buildPlaylist(itemToPlay),
//                    playWhenReady
//                )
//            }
//        }
//    }
//
//    private fun buildPlaylist(item: MediaMetadataCompat): List<MediaMetadataCompat> =
//        audioDataSource.audiosLiveData.value!!.map {
//            it.toMediaMetadataCompat()
//        }?.filter {
//            it.album == item.album
//        }?.sortedBy { it.trackNumber }
//
//
//    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit
//
//    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit
//}