package danbroid.demo.media2.media.client

import android.content.Context
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.customplayer.MediaPlayer
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.MediaSessionManager
import androidx.media2.session.SessionCommandGroup
import com.google.common.util.concurrent.ListenableFuture
import danbroid.demo.media2.media.*


class AudioClient(context: Context) {

  val context = context.applicationContext

  init {
    log.error("Created AudioClient")
  }

  private val _pauseEnabled = MutableLiveData<Boolean>()
  val pauseEnabled: LiveData<Boolean> = _pauseEnabled

  private val _connected = MutableLiveData<Boolean>(false)
  val connected: LiveData<Boolean> = _connected

  val controllerCallback = object : MediaBrowser.BrowserCallback() {


    override fun onPlaybackInfoChanged(
      controller: MediaController,
      info: MediaController.PlaybackInfo
    ) {
      log.warn("onPlaybackInfoChanged(): $info")
    }

    override fun onAllowedCommandsChanged(
      controller: MediaController,
      commands: SessionCommandGroup
    ) {
      log.debug("onAllowedCommandsChanged() $commands")
      commands.commands.forEach {
        log.debug("allowed command: $it")
      }
    }

    override fun onCurrentMediaItemChanged(controller: MediaController, item: MediaItem?) {
      log.debug("item: $item")
    }

    override fun onBufferingStateChanged(controller: MediaController, item: MediaItem, state: Int) {
      log.debug("onBufferingStateChanged() ${state.buffState}")
    }

    override fun onPlayerStateChanged(controller: MediaController, state: Int) {
      log.debug("onPlayerStateChanged() ${state.playerState}")
      _pauseEnabled.value  = state == SessionPlayer.PLAYER_STATE_PLAYING
    }

    override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
      log.warn("onConnected()")
      _connected.value = true
    }

    override fun onDisconnected(controller: MediaController) {
      log.warn("onDisconnected()")
      _connected.value = false
    }

    override fun onPlaylistChanged(
      controller: MediaController,
      list: MutableList<MediaItem>?,
      metadata: MediaMetadata?
    ) {
      log.warn("onPlaylistChanged() size: ${list?.size}")
    }
  }


  val executor = getMainExecutor(context)//Executors.newSingleThreadExecutor()

  val mediaController: MediaBrowser = let {

    val sessionManager = MediaSessionManager.getInstance(context)
    log.debug("got sessionManager: $sessionManager")
    val serviceToken = sessionManager.sessionServiceTokens.first {
      it.serviceName == AudioService::class.qualifiedName
    }

    MediaBrowser.Builder(context)
      .setControllerCallback(executor, controllerCallback)
      .setSessionToken(serviceToken)
      .build()
  }

  fun <T> ListenableFuture<T>.await(job: (T) -> Unit) {
    addListener({
      job.invoke(get())
    }, getMainExecutor(context))
  }

  fun playUri(uri: String) {
    log.trace("playUri() $uri")

    mediaController.addPlaylistItem(Integer.MAX_VALUE, uri).await {
      log.debug("result: $it code: ${it.resultCode} item:${it.mediaItem}")
      if (mediaController.playerState != MediaPlayer.PLAYER_STATE_PLAYING) {
        log.debug("skipping to start of playlist")
        mediaController.skipToPlaylistItem(0).addListener({
          mediaController.play()
        }, executor)

      }
    }

  }


  fun togglePause() {
    if (mediaController.playerState == MediaPlayer.PLAYER_STATE_PLAYING) {
      mediaController.pause()
    } else {
      mediaController.play()
    }
  }

  fun state() {
    log.info("buf: ${mediaController.bufferingState.buffState} state: ${mediaController.playerState.playerState}")
    //mediaController.adjustVolume(AudioManager.ADJUST_TOGGLE_MUTE, AudioManager.FLAG_PLAY_SOUND)
  }

  fun play() = mediaController.play().await {
    log.debug("play complete")
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


}

private val log = org.slf4j.LoggerFactory.getLogger(AudioClient::class.java)
