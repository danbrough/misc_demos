package danbroid.demo.media2.media.client

import android.content.Context
import android.media.AudioManager
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.core.net.toUri
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.player.MediaPlayer
import androidx.media2.session.*
import danbroid.demo.media2.media.AudioService
import danbroid.demo.media2.media.buffState
import danbroid.demo.media2.media.playerState
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor


class AudioClient(val context: Context) {

  val controllerCallback = object : MediaController.ControllerCallback() {

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
      log.debug("onConnected() allowedCommands: $allowedCommands")

      getMainExecutor(context).execute {
        val id = "https://h1.danbrough.org/guitar/improv/improv1.mp3"
        //val id = "http://ice4.somafm.com/u80s-256-mp3"
        mediaController.setMediaItem(id)
      }

    }

    override fun onPlaylistChanged(
      controller: MediaController,
      list: MutableList<MediaItem>?,
      metadata: MediaMetadata?
    ) {
      log.debug("onPlaylistChanged()")
    }
  }

  val executor = Executors.newSingleThreadExecutor()
  val mediaController: MediaController

  init {
    val sessionManager = MediaSessionManager.getInstance(context)
    log.debug("got sessionManager: $sessionManager")
    val serviceToken = sessionManager.sessionServiceTokens.first {
      it.serviceName == AudioService::class.qualifiedName
    }

    mediaController = MediaController.Builder(context)
      .setControllerCallback(executor, controllerCallback)
      .setSessionToken(serviceToken)
      .build()
    log.debug("created media controller: $mediaController")


  }

  fun play() = mediaController.play()

  fun prepare() {
    mediaController.prepare().addListener({
      log.debug("prepared")
    }, getMainExecutor(context))
  }

  fun togglePause() {
    mediaController.pause().addListener({
      log.debug("pause toggled")
    }, getMainExecutor(context))
  }

  fun state() {
    log.info("buf: ${mediaController.bufferingState.buffState} state: ${mediaController.playerState.playerState}")
    mediaController.adjustVolume(AudioManager.ADJUST_TOGGLE_MUTE, AudioManager.FLAG_PLAY_SOUND)
  }


}

private val log = org.slf4j.LoggerFactory.getLogger(AudioClient::class.java)
