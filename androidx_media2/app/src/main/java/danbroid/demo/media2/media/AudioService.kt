package danbroid.demo.media2.media

import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media.AudioAttributesCompat
import androidx.media2.common.*
import androidx.media2.customplayer.ExoPlayerMediaPlayer2Impl
import androidx.media2.customplayer.ExoPlayerWrapper
import androidx.media2.customplayer.MediaPlayer
import androidx.media2.player.StatsListener
import androidx.media2.session.LibraryResult
import androidx.media2.session.MediaLibraryService
import androidx.media2.session.MediaSession
import com.google.android.exoplayer2.ExoPlayerLibraryInfo
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import danbroid.demo.media2.R

class AudioService : MediaLibraryService() {


  val sessionCallback = SessionCallback()

  lateinit var player: SessionPlayer

  lateinit var session: MediaLibrarySession


  override fun onCreate() {
    log.info("onCreate()")
    super.onCreate()

    log.error("PLAYER VERSION: ${ExoPlayerLibraryInfo.VERSION_SLASHY}")
    player = MediaPlayer(applicationContext)

    player.setAudioAttributes(
      AudioAttributesCompat.Builder()
        .setUsage(AudioAttributesCompat.USAGE_MEDIA)
        .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
        .build()
    )

    val exoPlayerImpl2 = player.javaClass.getDeclaredField("mPlayer").let {
      it.isAccessible = true
      it.get(player) as ExoPlayerMediaPlayer2Impl
    }

    val exoPlayerWrapper = exoPlayerImpl2.javaClass.getDeclaredField("mPlayer").let {
      it.isAccessible = true
      it.get(exoPlayerImpl2) as ExoPlayerWrapper
    }

    val exoPlayer = exoPlayerWrapper.javaClass.getDeclaredField("mPlayer").let {
      it.isAccessible = true
      it.get(exoPlayerWrapper) as SimpleExoPlayer
    }

    exoPlayer.analyticsCollector.addListener(object : StatsListener {
      override fun onTracksChanged(
        eventTime: AnalyticsListener.EventTime,
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
      ) {
        log.error("onTracksChanged()")
        for (n in 0 until trackGroups.length) {
          for (m in 0 until trackGroups[n].length) {
            trackGroups[n].getFormat(m).also {
              log.error("METADATA: ${it.metadata}")
            }
          }
        }
      }

      override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
        log.error("onMetadata() $metadata")
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
            .putString(MediaMetadata.METADATA_KEY_TITLE, "Da Title")
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, "Da DisplayTitle")
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, "Da DisplaySubTitle")
            .putBitmap(
              MediaMetadata.METADATA_KEY_DISPLAY_ICON,
              BitmapFactory.decodeResource(
                resources,
                R.drawable.cast_album_art_placeholder
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
