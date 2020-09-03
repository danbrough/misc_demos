package danbroid.demo.media2.media.client

import android.content.Context
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.customplayer.MediaPlayer
import androidx.media2.session.MediaBrowser
import androidx.media2.session.MediaController
import androidx.media2.session.MediaSessionManager
import androidx.media2.session.SessionCommandGroup
import com.google.common.util.concurrent.ListenableFuture
import danbroid.demo.media2.media.AudioService
import danbroid.demo.media2.media.buffState
import danbroid.demo.media2.media.playerState


class AudioClient(context: Context) {

  val context = context.applicationContext

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

    override fun onBufferingStateChanged(controller: MediaController, item: MediaItem, state: Int) {
      log.debug("onBufferingStateChanged() ${state.buffState}")
    }

    override fun onPlayerStateChanged(controller: MediaController, state: Int) {
      log.debug("onPlayerStateChanged() ${state.playerState}")
    }

    override fun onConnected(controller: MediaController, allowedCommands: SessionCommandGroup) {
      log.warn("onConnected() allowedCommands: ${allowedCommands.commands}")
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
  val mediaController: MediaBrowser

  fun <T> ListenableFuture<T>.await(job: (T) -> Unit) {
    addListener({
      job.invoke(get())
    }, getMainExecutor(context))
  }

  init {
    val sessionManager = MediaSessionManager.getInstance(context)
    log.debug("got sessionManager: $sessionManager")
    val serviceToken = sessionManager.sessionServiceTokens.first {
      it.serviceName == AudioService::class.qualifiedName
    }

    mediaController = MediaBrowser.Builder(context)
      .setControllerCallback(executor, controllerCallback)
      .setSessionToken(serviceToken)
      .build()
    log.debug("created media controller: $mediaController")


  }


  fun playUri(uri: String) {
    log.trace("playUri() $uri")

    mediaController.addPlaylistItem(Integer.MAX_VALUE, uri).await {
      log.debug("result: $it code: ${it.resultCode} item:${it.mediaItem}")
      if (mediaController.playerState != MediaPlayer.PLAYER_STATE_PLAYING) {
        log.debug("skipping to start of playlist")
        mediaController.skipToPlaylistItem(0)
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
