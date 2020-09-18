package danbroid.media.service

import android.content.Context
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.common.SubtitleData
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.MediaSessionManager
import androidx.media2.session.SessionCommandGroup
import com.google.common.util.concurrent.ListenableFuture


class AudioClient(context: Context) {


  private val _pauseEnabled = MutableLiveData<Boolean>(false)
  val pauseEnabled: LiveData<Boolean> = _pauseEnabled

  private val _connected = MutableLiveData<Boolean>(false)
  val connected: LiveData<Boolean> = _connected

  private val _hasNext = MutableLiveData<Boolean>(false)
  val hasNext: LiveData<Boolean> = _hasNext

  private val _currentItem = MutableLiveData<MediaItem?>(null)
  val currentItem: LiveData<MediaItem?> = _currentItem

  private val _metadata = MutableLiveData<MediaMetadata?>(null)
  val metadata: LiveData<MediaMetadata?> = _metadata

  private val _hasPrevious = MutableLiveData<Boolean>(false)
  val hasPrevious: LiveData<Boolean> = _hasPrevious

  protected val controllerCallback = ControllerCallback()

  protected val mainExecutor = getMainExecutor(context)//Executors.newSingleThreadExecutor()

  val mediaController: MediaBrowser = let {

    val sessionManager = MediaSessionManager.getInstance(context)
    log.debug("got sessionManager: $sessionManager")
    val serviceToken = sessionManager.sessionServiceTokens.first {
      it.serviceName == AudioService::class.qualifiedName
    }

    MediaBrowser.Builder(context)
      .setControllerCallback(mainExecutor, controllerCallback)
      .setSessionToken(serviceToken)
      .build()
  }


  fun playUri(uri: String) {
    log.trace("playUri() $uri")


    mediaController.addPlaylistItem(Integer.MAX_VALUE, uri).then {
      log.debug("result: $it code: ${it.resultCode} item:${it.mediaItem}")
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


  fun callPlay() {
    log.trace("callPlay() state:${mediaController.playerState.playerState} buffState:${mediaController.bufferingState.buffState}")
    if (mediaController.playerState != SessionPlayer.PLAYER_STATE_PLAYING || mediaController.bufferingState == SessionPlayer.BUFFERING_STATE_BUFFERING_AND_PLAYABLE) {
      log.trace("calling mediaController.play()")
      mediaController.play()
    }
  }

  fun test() {
    log.debug("test()")
    mediaController.playlistMetadata?.also {
      log.info("metadata: $it")
    }
    mediaController.playlist?.forEach {
      log.debug("playlistItem: $it")
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
      log.debug("onPlaylistMetadataChanged() $metadata")
      _metadata.value = metadata
    }

    override fun onPlaylistChanged(
      controller: MediaController,
      list: MutableList<MediaItem>?,
      metadata: MediaMetadata?
    ) {
      val state = controller.playerState
      log.info("onPlaylistChanged() size:${list?.size} state:${state.playerState} prev:${controller.previousMediaItemIndex} next:${controller.nextMediaItemIndex} $metadata")
      _hasNext.value = controller.nextMediaItemIndex != -1
      _hasPrevious.value = controller.previousMediaItemIndex != -1
    }

    override fun onTrackSelected(controller: MediaController, trackInfo: SessionPlayer.TrackInfo) {
      log.info("onTrackSelected() ${controller.currentMediaItem?.metadata}")
    }


    override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
      log.info("onCurrentMediaItemChanged(): $item metadata: ${item?.metadata}")
      _currentItem.value = item
      _metadata.value = item?.metadata
      _hasNext.value = controller.nextMediaItemIndex != -1
      _hasPrevious.value = controller.previousMediaItemIndex != -1
    }

    override fun onBufferingStateChanged(controller: MediaController, item: MediaItem, state: Int) {
      log.info("onBufferingStateChanged() ${state.buffState}")
      if (state == SessionPlayer.BUFFERING_STATE_BUFFERING_AND_PLAYABLE) {

      }
    }

    override fun onPlayerStateChanged(controller: MediaController, state: Int) {
      super.onPlayerStateChanged(controller, state)
      log.info("onPlayerStateChanged() state:$state = ${state.playerState}")
      val pauseEnabled =
        (state == SessionPlayer.PLAYER_STATE_PLAYING)
      log.debug("pauseEnabled: ${pauseEnabled}")
      _pauseEnabled.value = pauseEnabled
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
    }

    override fun onDisconnected(controller: MediaController) {
      log.info("onDisconnected()")
      _connected.value = false
    }


  }
}

private val log = org.slf4j.LoggerFactory.getLogger(AudioClient::class.java)
