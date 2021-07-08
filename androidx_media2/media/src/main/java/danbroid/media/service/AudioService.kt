package danbroid.media.service

import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.media.AudioAttributesCompat
import androidx.media2.common.BaseResult
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.session.*
import androidx.palette.graphics.Palette
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerLibraryInfo
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.media2.SessionPlayerConnector
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.flac.VorbisComment
import com.google.android.exoplayer2.metadata.icy.IcyInfo
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.common.util.concurrent.ListenableFuture
import danbroid.media.BuildConfig
import kotlinx.coroutines.*
import java.util.concurrent.Executor

class AudioService : MediaSessionService() {

  companion object {
    const val PACKAGE = "danbroid.media.service"
    const val METADATA_EXTRAS_KEY_CACHED_ICON = "$PACKAGE.METADATA_EXTRAS_KEY_CACHED_ICON"
    //const val COMMAND_CLEAR_PLAYLIST = "$PACKAGE.CLEAR_PLAYLIST"

    const val MEDIA_METADATA_KEY_BITRATE =
        "danbroid.media.service.AudioService.MEDIA_METADATA_KEY_BITRATE"
    const val MEDIA_METADATA_KEY_LIGHT_COLOR =
        "danbroid.media.service.AudioService.MEDIA_METADATA_KEY_LIGHT_COLOR"
    const val MEDIA_METADATA_KEY_DARK_COLOR =
        "danbroid.media.service.AudioService.MEDIA_METADATA_KEY_DARK_COLOR"
    const val MEDIA_METADATA_KEY_LIGHT_MUTED_COLOR =
        "danbroid.media.service.AudioService.MEDIA_METADATA_KEY_LIGHT_MUTED_COLOR"
    const val MEDIA_METADATA_KEY_DARK_MUTED_COLOR =
        "danbroid.media.service.AudioService.MEDIA_METADATA_KEY_DARK_MUTED_COLOR"
    const val MEDIA_METADATA_KEY_DOMINANT_COLOR =
        "danbroid.media.service.AudioService.MEDIA_METADATA_KEY_DOMINANT_COLOR"
    const val MEDIA_METADATA_KEY_VIBRANT_COLOR =
        "danbroid.media.service.AudioService.MEDIA_METADATA_KEY_VIBRANT_COLOR"
  }

  val sessionCallback = SessionCallback()

  //lateinit var player: SessionPlayerConnector
  lateinit var exoPlayer: SimpleExoPlayer
  lateinit var session: MediaSession

  private lateinit var callbackExecutor: Executor
  private lateinit var notificationManager: PlayerNotificationManager
  internal val iconUtils = IconUtils(this)

  val defaultIcon: Bitmap by lazy {
    iconUtils.drawableToBitmapIcon(Config.Notifications.defaultNotificationIcon)
  }
  private val lifecycleScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
    log.ddebug("created player: $exoPlayer")


    val sessionPlayer = SessionPlayerConnector(exoPlayer)
    sessionPlayer.setAudioAttributes(
        AudioAttributesCompat.Builder()
            .setUsage(AudioAttributesCompat.USAGE_MEDIA)
            .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
            .build()
    )

    session =
        MediaSession.Builder(this, sessionPlayer)
            .setSessionCallback(callbackExecutor, sessionCallback)
            // .setId("danbroid.media.session")
            .build()


//    addSession(session)


    sessionPlayer.registerPlayerCallback(callbackExecutor, object : SessionPlayer.PlayerCallback() {


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
    return START_NOT_STICKY
  }

  override fun onDestroy() {
    super.onDestroy()
    log.info("onDestroy() hashCode:${hashCode()}")
    lifecycleScope.cancel("Service destroyed")
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


    /* exoPlayer.addListener(
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
         )
 */




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

    override fun onSetMediaUri(session: MediaSession, controller: MediaSession.ControllerInfo, uri: Uri, extras: Bundle?): Int {
      log.ddebug("onSetMediaUri() $uri")

/*
      val metadata = extras?.let { ParcelUtils.getVersionedParcelable<MediaMetadata?>(it, "item") }
      log.ddebug("metadata: ${metadata.toDebugString()}")

      runCatching {

        if (metadata != null) {
          session.player.setMediaItem(UriMediaItem.Builder(metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_URI)!!.toUri())
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
      }.exceptionOrNull()?.also {
        log.error("Failed to set media item: ${it.message}", it)
      }
*/

      return super.onSetMediaUri(session, controller, uri, extras)
    }

    override fun onCreateMediaItem(session: MediaSession, controller: MediaSession.ControllerInfo, mediaId: String): MediaItem? {
      log.debug("onCreateMediaItem() $mediaId")

      return runBlocking {
        audioServiceConfig.library.loadItem(mediaId)
      }?.also { loadIcon(it) } ?: super.onCreateMediaItem(session, controller, mediaId)
    }


    fun loadIcon(mediaItem: MediaItem) {
      log.error("loadIcon() $mediaItem")

      lifecycleScope.launch {
        fun updateMetadata(bitmap: Bitmap) {
          log.dwarn("updateMetadata()")

          val builder = MediaMetadata.Builder(mediaItem.metadata!!)

          val extras = mediaItem.metadata?.extras ?: bundleOf().also {
            builder.setExtras(it)
          }

          if (bitmap != defaultIcon && !extras.containsKey(METADATA_EXTRAS_KEY_CACHED_ICON)) {
            log.dwarn("generating palette............................................")

            val palette = Palette.from(bitmap).generate()

            extras.putInt(MEDIA_METADATA_KEY_LIGHT_COLOR, palette.getLightVibrantColor(Color.TRANSPARENT))
            extras.putInt(MEDIA_METADATA_KEY_DARK_COLOR, palette.getDarkVibrantColor(Color.TRANSPARENT))
            extras.putInt(MEDIA_METADATA_KEY_LIGHT_MUTED_COLOR, palette.getLightMutedColor(Color.TRANSPARENT))
            extras.putInt(MEDIA_METADATA_KEY_DARK_MUTED_COLOR, palette.getDarkMutedColor(Color.TRANSPARENT))
            extras.putInt(MEDIA_METADATA_KEY_DOMINANT_COLOR, palette.getDominantColor(Color.TRANSPARENT))
            extras.putInt(MEDIA_METADATA_KEY_VIBRANT_COLOR, palette.getVibrantColor(Color.TRANSPARENT))
          }

          extras.putParcelable(METADATA_EXTRAS_KEY_CACHED_ICON, bitmap)
          builder.putBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON, bitmap)
          mediaItem.metadata = builder.build()
        }

        iconUtils.loadIcon(mediaItem.metadata, defaultIcon) {
          updateMetadata(it)
        }?.also {
          updateMetadata(it)
        }
      }
    }


    override fun onCommandRequest(session: MediaSession, controller: MediaSession.ControllerInfo, command: SessionCommand): Int {
      log.debug("onCommandRequest() ${command.commandCode}:${command.customAction}:extras:${command.customExtras}")
      if (session.player.playerState == SessionPlayer.PLAYER_STATE_ERROR) {
        log.info("in the error state suppresion reason: ${exoPlayer.playbackSuppressionReason} error: ${exoPlayer.playerError}")
        session.player.prepare()
        //exoPlayer.prepare()
      }
      return super.onCommandRequest(session, controller, command)
    }


    override fun onConnect(session: MediaSession, controller: MediaSession.ControllerInfo): SessionCommandGroup = SessionCommandGroup.Builder().let { builder ->
      super.onConnect(session, controller)?.commands?.forEach {
        builder.addCommand(it)
      }
      builder.build()
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
      log.info("onPostConnect() session:$session controller:$controller")
      log.debug("controller uid: ${controller.uid} package: ${controller.packageName}")
      super.onPostConnect(session, controller)
    }

    override fun onCustomCommand(session: MediaSession, controller: MediaSession.ControllerInfo, customCommand: SessionCommand, args: Bundle?): SessionResult {
      log.debug("onCustomCommand(): ${customCommand.commandCode}:${customCommand.customAction}:extras:${customCommand.customExtras.toDebugString()} args: ${args.toDebugString()}")


      return SessionResult(BaseResult.RESULT_ERROR_NOT_SUPPORTED, null)
      //     return SessionResult(SessionResult.RESULT_SUCCESS, null)
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
      var title: String? = null
      var album: String? = null
//      val currentMetadata = player.currentMediaItem!!.metadata!!

      (0 until metadata.length()).forEach {
        val entry = metadata.get(it)

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
                album = entry.value
                log.dtrace("album: $album")
              }
            }
          }
          is IcyInfo -> {
            log.trace("ANALYTICS: IcyInfo title:${entry.title} ${entry.url}")
            title = entry.title
          }
        }
      }


      log.dtrace("playlistMetadata: ${session.player.playlistMetadata}")
      log.dtrace("currentItem.metadata: ${session.player.currentMediaItem?.metadata}")
      log.dtrace("title: $title")

      MediaMetadata.Builder(session.player.currentMediaItem!!.metadata!!)
          .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, title)
          .build().also {
            session.player.currentMediaItem!!.metadata = it
          }


      /*  val oldMetadata = session.player.playlistMetadata?.also {
          log.dtrace("using playlistMetadata")
        } ?: session.player.currentMediaItem?.metadata?.also {
          log.dtrace("using currentMediaItem metadata")
        }!!

        val oldTitle = oldMetadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
        if (oldTitle != title && title != null) {
          MediaMetadata.Builder(oldMetadata)
              .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, title)
              .build().also {
                log.dtrace("updating metadata with new title: $title")
                session.player.updatePlaylistMetadata(it)
              }
        }*/

    }

/*    override fun onTracksChanged(
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
                  log.derror("ANALYTICS: updating currentMediaItem metadata with title:$title")

                //  player.currentMediaItem?.metadata = newMetadata
                  player.updatePlaylistMetadata(newMetadata)
                }
              }
            }
          }
        }

      }

    }
    */

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