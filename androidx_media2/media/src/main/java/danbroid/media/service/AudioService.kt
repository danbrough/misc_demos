package danbroid.media.service

import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media.AudioAttributesCompat
import androidx.media2.common.*
import androidx.media2.session.LibraryResult
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.MediaSession
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerLibraryInfo
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.media2.SessionPlayerConnector
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.flac.VorbisComment
import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter

class AudioService : MediaLibraryService() {


  val sessionCallback = SessionCallback()

  lateinit var player: SessionPlayer

  lateinit var session: MediaLibrarySession


  override fun onCreate() {
    log.info("onCreate()")
    super.onCreate()

    log.debug("PLAYER VERSION: ${ExoPlayerLibraryInfo.VERSION_SLASHY}")


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
    val exoPlayer: SimpleExoPlayer = SimpleExoPlayer.Builder(
      this,
      renderersFactory
    )
      .setBandwidthMeter(
        DefaultBandwidthMeter.Builder(this)
          .build().also {
            it.addEventListener(Handler(), object : BandwidthMeter.EventListener {
              override fun onBandwidthSample(
                elapsedMs: Int,
                bytesTransferred: Long,
                bitrateEstimate: Long
              ) {
                log.warn("onBandwidth() $bytesTransferred bitrate:$bitrateEstimate")
              }

            })
          }
      )
      .build()

    player = SessionPlayerConnector(exoPlayer)
    exoPlayer.addListener(object : Player.EventListener {
      override fun onIsLoadingChanged(isLoading: Boolean) {
        log.info("onIsLoadingChanged() $isLoading")
      }

    })



    player.setAudioAttributes(
      AudioAttributesCompat.Builder()
        .setUsage(AudioAttributesCompat.USAGE_MEDIA)
        .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
        .build()
    )

    exoPlayer.analyticsCollector.addListener(object : AnalyticsListener {
      override fun onTracksChanged(
        eventTime: AnalyticsListener.EventTime,
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
      ) {
        log.warn("onTracksChanged()")
        for (n in 0 until trackGroups.length) {
          for (m in 0 until trackGroups[n].length) {

            trackGroups[n].getFormat(m).also { format ->
              val metadata = format.metadata
              val currentMetadata = player.currentMediaItem?.metadata
              log.error("bitrate: ${format.bitrate}")
              log.error("trackMetadata:$n cls:${format::class.java} ${metadata}")
              log.error("currentMetadata: ${currentMetadata}")

              var title: String? = null

              if (metadata != null) {
                (0 until metadata.length()).forEach {
                  val entry = metadata.get(it)
                  log.error("entry: ${entry} cls: ${entry::class.java}")
                  when (entry) {
                    is VorbisComment -> {
                      log.error("VORBIS COMMENT: ${entry.key}:=<${entry.value}>")
                      when (entry.key) {
                        "Title" -> {
                          title = entry.value
                        }
                      }
                    }
                    is TextInformationFrame -> {
                      log.error("ID3 COMMENT: ${entry.id}:=<${entry.value}>")
                      when (entry.id) {
                        "TIT2" -> {
                          title = entry.value
                        }
                        "TALB" -> {
                          val album = entry.value
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
                    log.error("updating playlist metadata with title:$title")

                    player.currentMediaItem?.metadata = newMetadata
                    player.updatePlaylistMetadata(newMetadata)
                  }
                }
              }

            }
          }
        }
      }

      override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
        log.warn("onMetadata() $metadata")
        val currentMetadata = player.currentMediaItem?.metadata
        val oldMetadata = TrackMetadata(currentMetadata)
        val newMetadata = metadata.toTrackMetadata(currentMetadata)
        if (oldMetadata != newMetadata) {
          log.error("UPDATING currentItems metadata with: $newMetadata")
          newMetadata.toMediaMetadata().also {
            player.currentMediaItem?.metadata = it
            player.updatePlaylistMetadata(it)
          }

        }

        /*   (0 until metadata.length()).forEach {
             metadata[it].also { entry ->
               log.error("entry: cls:${entry::class.java} $it: format:${entry.wrappedMetadataFormat}")


               if (entry is IcyInfo) {
                 log.error("TITLE: ${entry.title} URL: ${entry.url}")
                 val md = player.currentMediaItem?.metadata
                 log.error("EXISTING METADATA: $md")
                 if (md != null) {
                   val newMetadata = MediaMetadata.Builder(md).also {
                     it.putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, entry.title)
                   }.build()
                   player.updatePlaylistMetadata(newMetadata)
                 }


               }
             }
           }
           player.playlistMetadata?.also { entry ->
             log.error("current data: $entry")
           }*/

      }

      override fun onBandwidthEstimate(
        eventTime: AnalyticsListener.EventTime,
        totalLoadTimeMs: Int,
        totalBytesLoaded: Long,
        bitrateEstimate: Long
      ) {
        log.error("loadTime: $totalLoadTimeMs totalBytesLoaded:$totalBytesLoaded bitrateEstimate:$bitrateEstimate")
      }
    })


    /*player.registerPlayerCallback({}, object : SessionPlayer.PlayerCallback() {
      override fun onSubtitleData(
        player: SessionPlayer,
        item: MediaItem,
        track: SessionPlayer.TrackInfo,
        data: SubtitleData
      ) {
        log.error("SUBTITLE DATA: $data")
      }
    })*/

    log.info("created player: $player")

    //PlayerUtils.configure(player)


    val executor = ContextCompat.getMainExecutor(this)

    session =
      MediaLibrarySession.Builder(this, player, executor, sessionCallback)
        .setId("session")
        .build()


  }

  override fun startForegroundService(service: Intent): ComponentName? {
    log.warn("startForegroundService(): $service")
    return super.startForegroundService(service)
  }


  override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
    log.info("onGetSession() controllerInfo: $controllerInfo")
    return session
  }

  inner class SessionCallback : MediaLibrarySession.MediaLibrarySessionCallback() {
    override fun onGetLibraryRoot(
      session: MediaLibrarySession,
      controller: MediaSession.ControllerInfo,
      params: LibraryParams?
    ): LibraryResult {
      log.error("onGetLibraryRoot()  params: ${params}")
      val root = super.onGetLibraryRoot(session, controller, params)
      log.debug("returning $root")
      return root
    }

    override fun onSubscribe(
      session: MediaLibrarySession,
      controller: MediaSession.ControllerInfo,
      parentId: String,
      params: LibraryParams?
    ): Int {
      log.warn("onSubscribe() $parentId")
      return BaseResult.RESULT_SUCCESS
    }


    override fun onCreateMediaItem(
      session: MediaSession,
      controller: MediaSession.ControllerInfo,
      mediaId: String
    ): MediaItem? {
      log.error("onCreateMediaItem() $mediaId")


      return UriMediaItem.Builder(mediaId.toUri())
        .setStartPosition(0L).setEndPosition(-1L)
        .setMetadata(
          MediaMetadata.Builder()
            .putLong(MediaMetadata.METADATA_KEY_PLAYABLE, 1)
            .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, mediaId)
/*            .putString(MediaMetadata.METADATA_KEY_TITLE, "Da Title")
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, "Da DisplayTitle")
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, "Da DisplaySubTitle")*/
            .putBitmap(
              MediaMetadata.METADATA_KEY_DISPLAY_ICON,
              BitmapFactory.decodeResource(
                resources,
                danbroid.media.R.drawable.ic_play
              )
            )
            .putString(MediaMetadata.METADATA_KEY_MEDIA_URI, mediaId)
            .build()
        )
        .build()
    }

    override fun onGetItem(
      session: MediaLibrarySession,
      controller: MediaSession.ControllerInfo,
      mediaId: String
    ): LibraryResult {
      log.error("onGetItem() id: $mediaId")
      return super.onGetItem(session, controller, mediaId)
    }

    override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
      log.info("onPostConnect() session:$session controller:$controller")
      log.debug("controller uid: ${controller.uid} package: ${controller.packageName}")
      super.onPostConnect(session, controller)
    }
  }
}


private val log = org.slf4j.LoggerFactory.getLogger(AudioService::class.java)
