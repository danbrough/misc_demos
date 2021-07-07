package danbroid.media.client

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.common.SubtitleData
import androidx.media2.session.*
import androidx.versionedparcelable.ParcelUtils
import com.google.common.util.concurrent.ListenableFuture
import danbroid.media.service.AudioService
import danbroid.media.service.buffState
import danbroid.media.service.playerState
import danbroid.media.service.toDebugString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


open class AudioClient(context: Context) {

  enum class PlayerState {
    IDLE, PAUSED, PLAYING, ERROR
  }

  enum class BufferingState {
    UNKNOWN, BUFFERING_AND_PLAYABLE, BUFFERING_AND_STARVED, BUFFERING_COMPLETE;
  }

  data class QueueState(val hasPrevious: Boolean, val hasNext: Boolean, val playState: PlayerState, val size: Int)

  private val _queueState = MutableStateFlow(QueueState(false, false, PlayerState.IDLE, 0))
  val queueState: StateFlow<QueueState> = _queueState

  private val _bufferingState = MutableStateFlow(BufferingState.UNKNOWN)
  val bufferingState: StateFlow<BufferingState> = _bufferingState

  private val _playState = MutableStateFlow(PlayerState.IDLE)
  val playState: StateFlow<PlayerState> = _playState

  private val _connected = MutableStateFlow(false)
  val connected: StateFlow<Boolean> = _connected

  private val _currentItem = MutableStateFlow<MediaItem?>(null)
  val currentItem: StateFlow<MediaItem?> = _currentItem

  private val _metadata = MutableStateFlow<MediaMetadata?>(null)
  val metadata: StateFlow<MediaMetadata?> = _metadata

  protected val controllerCallback = ControllerCallback()

  protected val mainExecutor = ContextCompat.getMainExecutor(context)//Executors.newSingleThreadExecutor()
  // protected val mainExecutor = java.util.concurrent.Executors.newSingleThreadExecutor()

  val mediaController: MediaBrowser = run {


    log.debug("starting service ..")
    context.startService(Intent(context, AudioService::class.java))


    val sessionManager = MediaSessionManager.getInstance(context)
    val serviceToken = sessionManager.sessionServiceTokens.first {
      it.serviceName == AudioService::class.qualifiedName
    }

    log.derror("serviceToken: $serviceToken.")


    MediaBrowser.Builder(context)
        .setControllerCallback(mainExecutor, controllerCallback)
        .setSessionToken(serviceToken)
        .build()
  }


  fun playUri(uri: String) {
    log.info("playUri() $uri")

    mediaController.playlist?.indexOfFirst {
      it.metadata?.mediaId == uri
    }?.also {
      if (it != -1) {
        if (it != mediaController.currentMediaItemIndex) {
          log.dtrace("skipping to existing item $it")

          mediaController.skipToPlaylistItem(it).then {
            if (it.resultCode == SessionResult.RESULT_SUCCESS) {
              log.trace("calling play")
              mediaController.play()
            } else {
              log.error("failed to skip to existing playlist item: ${it.customCommandResult}")
            }
          }
        } else {
          mediaController.play()
        }
        return
      }
    }

    log.dinfo("adding to playlist")


    mediaController.addPlaylistItem(Integer.MAX_VALUE, uri).then {
      log.ddebug("result: $it code: ${it.resultCode} item:${it.mediaItem}")
      if (mediaController.playerState != SessionPlayer.PLAYER_STATE_PLAYING) {
        mediaController.play()
      }
    }
  }


  fun togglePause() {
    log.debug("togglePause() state: ${mediaController.playerState.playerState}")
    if (mediaController.playerState == SessionPlayer.PLAYER_STATE_PLAYING) {
      mediaController.pause()
    } else {
      mediaController.play()
    }
  }

  fun skipToNext() {
    mediaController.skipToNextPlaylistItem()
  }

  fun skipToPrev() {
    mediaController.skipToPreviousPlaylistItem()
  }

  private fun <T> ListenableFuture<T>.then(job: (T) -> Unit) =
      addListener({
        job.invoke(get())
      }, mainExecutor)


  fun test(item: MediaMetadata) {
    log.dinfo("test()")
    val metadata = mediaController.playlistMetadata
    log.trace("metadata: ${metadata.toDebugString()}")
    val playlist = mediaController.playlist ?: mutableListOf()
    playlist.forEach {
      log.debug("playlist item: $it")
    }

    mediaController.setMediaUri(item.mediaId!!.toUri(), bundleOf().also {
      ParcelUtils.putVersionedParcelable(it, "item", item)
    }).then {
      log.debug("finished setting media uri")
    }


    /*  mediaController.sendCustomCommand(SessionCommand("test", bundleOf("id" to "thang")), bundleOf("count" to 3)).then {
        log.debug("cmd sent")
      }*/
  }


  protected inner class ControllerCallback : MediaBrowser.BrowserCallback() {

    override fun onPlaybackInfoChanged(
        controller: MediaController,
        info: MediaController.PlaybackInfo
    ) {
      log.info("onPlaybackInfoChanged(): $info")
    }

    override fun onPlaybackCompleted(controller: MediaController) {
      log.info("onPlaybackCompleted()")
    }

    override fun onPlaylistMetadataChanged(controller: MediaController, metadata: MediaMetadata?) {
      log.debug("onPlaylistMetadataChanged() metadata: ${metadata.toDebugString()}")
      _metadata.value = metadata
    }

    override fun onPlaylistChanged(
        controller: MediaController,
        list: MutableList<MediaItem>?,
        metadata: MediaMetadata?
    ) {
      val state = controller.playerState
      log.info("onPlaylistChanged() size:${list?.size ?: "null"} state:${state.playerState} prev:${controller.previousMediaItemIndex} next:${controller.nextMediaItemIndex}")
      log.info("metadata: ${metadata.toDebugString()}")
      _queueState.value = _queueState.value.copy(
          hasPrevious = controller.previousMediaItemIndex != -1,
          hasNext = controller.nextMediaItemIndex != -1,
          size = list?.size ?: 0
      )
    }

    override fun onTrackSelected(controller: MediaController, trackInfo: SessionPlayer.TrackInfo) {
      log.info("onTrackSelected() ${controller.currentMediaItem?.metadata}")
    }

    override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
      log.info("onCurrentMediaItemChanged(): $item metadata: ${item?.metadata}")

      log.dtrace("keys: ${item?.metadata?.keySet()?.joinToString(",")}")
      log.dtrace("extra keys: ${item?.metadata?.extras?.keySet()?.joinToString(",")}")

      _currentItem.value = item
      _metadata.value = item?.metadata
      _queueState.value = _queueState.value.copy(
          hasPrevious = controller.previousMediaItemIndex != -1,
          hasNext = controller.nextMediaItemIndex != -1
      )
    }

    override fun onBufferingStateChanged(controller: MediaController, item: MediaItem, state: Int) {
      log.info("onBufferingStateChanged() ${state.buffState}")

      _bufferingState.value = when (state) {
        SessionPlayer.BUFFERING_STATE_UNKNOWN -> BufferingState.UNKNOWN
        SessionPlayer.BUFFERING_STATE_BUFFERING_AND_PLAYABLE -> BufferingState.BUFFERING_AND_PLAYABLE
        SessionPlayer.BUFFERING_STATE_BUFFERING_AND_STARVED -> BufferingState.BUFFERING_AND_STARVED
        SessionPlayer.BUFFERING_STATE_COMPLETE -> BufferingState.BUFFERING_COMPLETE
        else -> error("Unknown buffering state: $state")
      }
    }

    override fun onPlayerStateChanged(controller: MediaController, state: Int) {
      log.debug("onPlayerStateChanged() state:$state = ${state.playerState}")
      _playState.value = when (state) {
        SessionPlayer.PLAYER_STATE_IDLE -> PlayerState.IDLE
        SessionPlayer.PLAYER_STATE_PLAYING -> PlayerState.PLAYING
        SessionPlayer.PLAYER_STATE_ERROR -> PlayerState.ERROR
        SessionPlayer.PLAYER_STATE_PAUSED -> PlayerState.PAUSED
        else -> error("Unknown player state: $state")
      }
    }

    override fun onSubtitleData(
        controller: MediaController,
        item: MediaItem,
        track: SessionPlayer.TrackInfo,
        data: SubtitleData
    ) {
      log.info("onSubtitleData() $track data: $data")
    }

    override fun onTracksChanged(
        controller: MediaController,
        tracks: MutableList<SessionPlayer.TrackInfo>
    ) {
      val state = controller.playerState
      log.info("onTracksChanged() tracks:${tracks} state:${state.playerState} prev:${controller.previousMediaItemIndex} next:${controller.nextMediaItemIndex}")

    }

    override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
      log.info("onConnected()")
      _connected.value = true
      _currentItem.value = controller.currentMediaItem
      _metadata.value = controller.currentMediaItem?.metadata

    }

    override fun onDisconnected(controller: MediaController) {
      log.info("onDisconnected()")
      _connected.value = false
    }


  }

  fun close() {
    log.info("close()")
    mediaController.close()
  }

  fun clearPlaylist() {
    log.trace("clearPlaylist()")
    if (!mediaController.playlist.isNullOrEmpty()) {
      mediaController.removePlaylistItem(0).then {
        clearPlaylist()
      }
    }
  }
}


private val log = danbroid.logging.getLog(AudioClient::class)