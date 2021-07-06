package danbroid.media.service

import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media.AudioAttributesCompat
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.common.UriMediaItem
import androidx.media2.session.*
import androidx.versionedparcelable.ParcelUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.media2.SessionPlayerConnector
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.flac.VorbisComment
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.common.util.concurrent.ListenableFuture
import danbroid.media.BuildConfig
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executor

class AudioService : MediaSessionService() {

  companion object {
    const val PACKAGE = "danbroid.media.service"
    const val COMMAND_PLAY_ITEM = "$PACKAGE.PLAY_ITEM"
  }

  val sessionCallback = SessionCallback()

  lateinit var player: SessionPlayerConnector
  lateinit var exoPlayer: SimpleExoPlayer

  lateinit var session: MediaSession

  private lateinit var callbackExecutor: Executor
  private lateinit var notificationManager: PlayerNotificationManager

  override fun onCreate() {
    log.derror("onCreate() hashCode:${hashCode()}")
    super.onCreate()

    log.info("ExoPlayerLibraryInfo.VERSION_SLASHY = ${ExoPlayerLibraryInfo.VERSION_SLASHY}")

    callbackExecutor = ContextCompat.getMainExecutor(this)

    /**
     * SessionPlayerConnector​(Player player)
    Creates an instance using DefaultMediaItemConverter to convert between ExoPlayer and media2 MediaItems and DefaultControlDispatcher to dispatch player commands.
    SessionPlayerConnector​(Player player, MediaItemConverter mediaItemConverter)
     */


    createExternalExoPlayer()
    log.ddebug("created player: $player")


    session =
        MediaSession.Builder(this, player)
            .setSessionCallback(callbackExecutor, sessionCallback)
            // .setId("danbroid.media.session")
            .build()


//    addSession(session)


    player.setAudioAttributes(
        AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .build()
    )




    player.registerPlayerCallback(callbackExecutor, object : SessionPlayer.PlayerCallback() {


/*      override fun onCurrentMediaItemChanged(player: SessionPlayer, item: MediaItem) {
        log.warn("onCurrentMediaItemChanged() $item")
      }*/

      override fun onPlaybackCompleted(player: SessionPlayer) {
        log.warn("onPlaybackCompleted()")
      }

      override fun onPlayerStateChanged(player: SessionPlayer, playerState: Int) {
        log.warn("onPlayerStateChanged() $playerState = ${playerState.playerState}")
      }

      override fun onPlaylistMetadataChanged(player: SessionPlayer, metadata: MediaMetadata?) {
        log.warn("onPlaylistMetadataChanged() $metadata")
      }

      override fun onTrackSelected(player: SessionPlayer, trackInfo: SessionPlayer.TrackInfo) {
        log.warn("onTrackSelected() ${trackInfo.format}")
      }

      override fun onTrackDeselected(player: SessionPlayer, trackInfo: SessionPlayer.TrackInfo) {
        log.warn("onTrackDeselected() $trackInfo")
      }

      override fun onTracksChanged(
          player: SessionPlayer,
          tracks: MutableList<SessionPlayer.TrackInfo>
      ) {
        log.warn("onTracksChanged()")
      }
    })
  }


  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    log.dwarn("onStartCommand() hashCode:${hashCode()}")
    super.onStartCommand(intent, flags, startId)
    return START_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    log.info("onDestroy() hashCode:${hashCode()}")
  }

  fun createExternalExoPlayer(): ExoPlayer {


/*
    val ffmpegRenderersFactory = object : RenderersFactory {
      override fun createRenderers(
        eventHandler: Handler,
        videoRendererEventListener: VideoRendererEventListener,
        audioRendererEventListener: AudioRendererEventListener,
        textRendererOutput: TextOutput,
        metadataRendererOutput: MetadataOutput
      ) = arrayOf(FfmpegAudioRenderer(eventHandler, audioRendererEventListener).also {
      })
    }
*/


    val defaultRenderersFactory =
        DefaultRenderersFactory(this).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
    val renderersFactory = defaultRenderersFactory

    val bwMeter = DefaultBandwidthMeter.Builder(this)
        .setResetOnNetworkTypeChange(true)
        .build().also {
          it.addEventListener(
              Handler(Looper.getMainLooper()),
              object : BandwidthMeter.EventListener {

                override fun onBandwidthSample(
                    elapsedMs: Int,
                    bytesTransferred: Long,
                    bitrateEstimate: Long
                ) {
                  log.warn("onBandwidth() $bytesTransferred bitrate:$bitrateEstimate")
                }

              })
        }

    exoPlayer = SimpleExoPlayer.Builder(this, renderersFactory)
        .setHandleAudioBecomingNoisy(true)
        .setBandwidthMeter(bwMeter)
        .build()

    player = SessionPlayerConnector(exoPlayer)
    var foreground = false

    notificationManager =
        createNotificationManager(this, notificationListener = object : NotificationListener {

          override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            log.warn("onNotificationCancelled() byUser:$dismissedByUser")
            if (dismissedByUser) {
              log.warn("SHOULD STOP PLAYBACK")
            } else {
              if (foreground) {
                log.warn("stopping foreground ..")
                stopForeground(true)
                foreground = false
              }
            }
          }


          override fun onNotificationPosted(
              notificationId: Int,
              notification: Notification,
              ongoing: Boolean
          ) {
            log.warn("onNotificationPosted() ongoing:$ongoing")
            if (ongoing) {
              if (!foreground) {
                log.warn("starting foreground ..")
                // startForegroundService(Intent(applicationContext, javaClass))
                ContextCompat.startForegroundService(applicationContext, Intent(applicationContext, javaClass))
                /*startForegroundService(
                     Intent(applicationContext, javaClass)
                 )*/
                startForeground(notificationId, notification)
                foreground = true
              }
            } else {
              if (foreground) {
                log.warn("stopping foreground ..")
                stopForeground(false)
                foreground = false
              }
            }
          }
        })

    notificationManager.setPlayer(exoPlayer)

    exoPlayer.addAnalyticsListener(ExoAnalyticsListener())


    exoPlayer.addListener(
        object : Player.Listener {
          val listenerDebug = false

          override fun onMetadata(metadata: Metadata) {
            if (listenerDebug) log.debug("Listener.onMetadata() $metadata")
          }

          override fun onPlayWhenReadyChanged(
              playWhenReady: Boolean, @Player.PlayWhenReadyChangeReason
              reason: Int
          ) {
            if (listenerDebug) log.debug("Listener.onPlayWhenReadyChanged(): ready:$playWhenReady} reason:$reason : ${reason.playWhenReadyChangeReason}")
          }

          override fun onPlaybackStateChanged(@Player.State state: Int) {
            if (listenerDebug) log.debug("Listener.onPlaybackStateChanged(): state:$state = ${state.exoPlayerState}")
          }

          override fun onIsLoadingChanged(isLoading: Boolean) {
            if (listenerDebug) log.debug("Listener.onIsLoadingChanged() $isLoading")
          }
        })


    player.setAudioAttributes(
        AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .build()
    )

    return exoPlayer
  }

  override fun startForegroundService(service: Intent): ComponentName? {
    log.info("startForegroundService(): $service")
    return super.startForegroundService(service)
  }


  override fun onUpdateNotification(session: MediaSession): MediaNotification? {
    val notification = super.onUpdateNotification(session)
    log.derror("onUpdateNotification() $notification")

    return null
  }

  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
    log.ddebug("onGetSession() controllerInfo: $controllerInfo")
    return session
  }


  inner class SessionCallback : MediaSession.SessionCallback() {
/*

    override fun onGetLibraryRoot(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        params: LibraryParams?
    ): LibraryResult {
      log.dinfo("onGetLibraryRoot()  params: ${params}")
      val root = super.onGetLibraryRoot(session, controller, params)
      log.dtrace("returning $root")
      return root
    }

    override fun onSubscribe(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        parentId: String,
        params: LibraryParams?
    ): Int {
      log.info("onSubscribe() $parentId")
      return BaseResult.RESULT_SUCCESS
    }
*/


/*    override fun onCreateMediaItem(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaId: String
    ): MediaItem? {
      log.debug("onCreateMediaItem() $mediaId")

      val trackMetadata = loadTestData(mediaId)
      trackMetadata ?: return null


      return UriMediaItem.Builder(mediaId.toUri())
          .setStartPosition(0L).setEndPosition(-1L)
          .setMetadata(
              trackMetadata.toMediaMetadata().putLong(MediaMetadata.METADATA_KEY_PLAYABLE, 1).build()
          )
          .build()
    }

    override fun onGetItem(
        session: MediaLibrarySession,
        controller: MediaSession.ControllerInfo,
        mediaId: String
    ): LibraryResult {
      log.dtrace("onGetItem() id: $mediaId")
      return super.onGetItem(session, controller, mediaId)
    }*/


    override fun onSetMediaUri(session: MediaSession, controller: MediaSession.ControllerInfo, uri: Uri, extras: Bundle?): Int {
      log.ddebug("onSetMediaUri() $uri")

      val metadata = extras?.let { ParcelUtils.getVersionedParcelable<MediaMetadata?>(it, "item") }
      log.ddebug("metadata: ${metadata.toDebugString()}")

      if (metadata != null) {
        player.setMediaItem(UriMediaItem.Builder(metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_URI)!!.toUri())
            .setStartPosition(0L).setEndPosition(-1L)
            .setMetadata(metadata)
            .build()).then {
          log.debug("result: $it")
          if (it.resultCode == SessionPlayer.PlayerResult.RESULT_SUCCESS) {
            log.debug("calling play")
            session.player.play()
          } else {
            log.error("failed: ${it.resultCode} item: ${it.mediaItem}")
          }
        }

        return SessionResult.RESULT_SUCCESS
      }

      return super.onSetMediaUri(session, controller, uri, extras)
    }

    override fun onCreateMediaItem(session: MediaSession, controller: MediaSession.ControllerInfo, mediaId: String): MediaItem? {
      log.debug("onCreateMediaItem() $mediaId")

      return runBlocking {
        audioServiceConfig.library.loadItem(mediaId)
            ?: super.onCreateMediaItem(session, controller, mediaId)
      }

    }


    override fun onCommandRequest(session: MediaSession, controller: MediaSession.ControllerInfo, command: SessionCommand): Int {
      log.debug("onCommandRequest() ${command.commandCode}:${command.customAction}:extras:${command.customExtras}")
      return super.onCommandRequest(session, controller, command)
    }

    override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): SessionCommandGroup = SessionCommandGroup.Builder().let { builder ->
      super.onConnect(session, controller)?.commands?.forEach {
        builder.addCommand(it)
      }
      builder.addCommand(SessionCommand("test", bundleOf("age" to 12)))
      builder.build()
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
      log.info("onPostConnect() session:$session controller:$controller")
      log.debug("controller uid: ${controller.uid} package: ${controller.packageName}")
      super.onPostConnect(session, controller)
    }

    override fun onCustomCommand(session: MediaSession, controller: MediaSession.ControllerInfo, customCommand: SessionCommand, args: Bundle?): SessionResult {
      log.debug("onCustomCommand(): ${customCommand.commandCode}:${customCommand.customAction}:extras:${customCommand.customExtras.toDebugString()} args: ${args.toDebugString()}")

      return SessionResult(SessionResult.RESULT_SUCCESS, null)
    }
  }


  inner class ExoAnalyticsListener : AnalyticsListener {
    override fun onBandwidthEstimate(
        eventTime: AnalyticsListener.EventTime,
        totalLoadTimeMs: Int,
        totalBytesLoaded: Long,
        bitrateEstimate: Long
    ) {
      log.error("ANALYTICS: loadTime: $totalLoadTimeMs totalBytesLoaded:$totalBytesLoaded bitrateEstimate:$bitrateEstimate")
    }


    override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
      log.derror("ANALYTICS: metadata $metadata")
      val currentMetadata = player.currentMediaItem!!.metadata!!
      val oldMetadata = TrackMetadata(currentMetadata)
      val newMetadata = metadata.toTrackMetadata(currentMetadata)
      if (oldMetadata != newMetadata) {
        log.dtrace("ANALYTICS: UPDATING currentItems metadata with: $newMetadata")
        newMetadata.toMediaMetadata().build().also {
          player.currentMediaItem?.metadata = it
          player.updatePlaylistMetadata(it)
          notificationManager.invalidate()
          /*          player.updatePlaylistMetadata(it).addListener({
                      log.warn("UPDATED PLAYLIST METADATA")
                      log.trace("playlistMetadata_title: ${player.playlistMetadata?.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)}")
                      log.trace(
                        "playlist.currentItem.metadata.title: ${
        player.currentMediaItem?.metadata?.getText(
            MediaMetadata.METADATA_KEY_DISPLAY_TITLE
        )
      }"
                      )

                      this@AudioService.onUpdateNotification(session)
                    }, ContextCompat.getMainExecutor(this@AudioService))*/
        }

      } else {
        log.derror("NO CHANGE IN METADATA")
      }
    }

    override fun onTracksChanged(
        eventTime: AnalyticsListener.EventTime,
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
      log.dwarn("ANALYTICS onTracksChanged()")
      for (n in 0 until trackGroups.length) {
        for (m in 0 until trackGroups[n].length) {
          trackGroups[n].getFormat(m).also { format ->
            val metadata = format.metadata
            val currentMetadata = player.currentMediaItem?.metadata
            log.dtrace("ANALYTICS:bitrate: ${format.bitrate}")
            log.dtrace("ANALYTICS:trackMetadata:$n cls:${format::class.java} ${metadata}")
            log.dtrace("ANALYTICS:currentMetadata: ${currentMetadata}")

            var title: String? = null

            if (metadata != null) {
              (0 until metadata.length()).forEach {
                val entry = metadata.get(it)
                log.dtrace("ANALYTICS: entry: ${entry} cls: ${entry::class.java}")
                when (entry) {
                  is VorbisComment -> {
                    log.dtrace("ANALYTICS:VORBIS COMMENT: ${entry.key}:=<${entry.value}>")
                    when (entry.key) {
                      "Title" -> {
                        title = entry.value
                      }
                    }
                  }
                  is TextInformationFrame -> {
                    log.dtrace("ANALYTICS: ID3 COMMENT: ${entry.id}:=<${entry.value}>")
                    when (entry.id) {
                      "TIT2" -> {
                        title = entry.value
                      }
                      "TALB" -> {
                        val album = entry.value
                        log.dinfo("album: $album")
                      }
                    }
                  }
                }
              }

              if (currentMetadata != null && title != null) {
                val oldTitle =
                    currentMetadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                if (oldTitle != title) {
                  val newMetadata = MediaMetadata.Builder(currentMetadata).also {
                    it.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title)
                  }.build()
                  log.dtrace("ANALYTICS: updating currentMediaItem metadata with title:$title")

                  player.currentMediaItem?.metadata = newMetadata
                  player.updatePlaylistMetadata(newMetadata)
                }
              }
            }
          }
        }
      }
    }
  }

  private fun <T> ListenableFuture<T>.then(job: (T) -> Unit) =
      addListener({
        job.invoke(get())
      }, callbackExecutor)
}

private fun Bundle?.toDebugString() = if (BuildConfig.DEBUG) this?.let { data ->
  data.keySet().joinToString(",") {
    "$it:${data[it]}"
  }.let {
    "Bundle<$it>"
  }
} ?: "null"
else this.toString()


fun MediaMetadata?.toDebugString(): String = if (BuildConfig.DEBUG) this.run {
  if (this != null) {
    keySet().joinToString {
      var s: String? = null
      runCatching {
        s = getString(it)
      }.exceptionOrNull()?.also {
        s = "<$it>"
      }
      s ?: "null"
    }.let { "MediaMetadata<${mediaId}:$it:extras:${extras.toDebugString()}>" }
  } else "MediaMetadata<null>"
} else "Metadata"


private val log = danbroid.logging.getLog(AudioService::class)