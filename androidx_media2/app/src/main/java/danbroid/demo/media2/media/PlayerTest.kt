package danbroid.demo.media2.media

import android.content.Context
import android.os.Looper
import androidx.core.net.toUri
import androidx.media2.common.*
import androidx.media2.customplayer.ExoPlayerWrapper
import androidx.media2.customplayer.ExoPlayerWrapper.Listener
import androidx.media2.customplayer.MediaPlayer2
import androidx.media2.customplayer.MediaTimestamp
import androidx.media2.customplayer.TimedMetaData
import androidx.media2.player.StatsListener
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

abstract class PlayerTest(val context: Context) {
  abstract fun play(uri: String)
  abstract fun togglePause()
}

class ExoWrapperTest(context: Context) : PlayerTest(context) {
  val player: ExoPlayerWrapper by lazy {
    val listener = object : Listener {
      override fun onPrepared(mediaItem: MediaItem?) {
        log.warn("onPrepared()")
      }

      override fun onTracksChanged(tracks: MutableList<SessionPlayer.TrackInfo>) {
        log.warn("onTracksChanged")
      }

      override fun onSeekCompleted() {
        log.warn("onSeekCompleted")
      }

      override fun onBufferingStarted(mediaItem: MediaItem?) {
        log.warn("onBufferingStarted")

      }

      override fun onBufferingEnded(mediaItem: MediaItem?) {
        log.warn("onBufferingEnded")

      }

      override fun onBufferingUpdate(mediaItem: MediaItem?, bufferingPercentage: Int) {

      }

      override fun onBandwidthSample(mediaItem2: MediaItem?, bitrateKbps: Int) {
      }

      override fun onVideoRenderingStart(mediaItem: MediaItem?) {
      }

      override fun onVideoSizeChanged(mediaItem: MediaItem?, width: Int, height: Int) {
      }

      override fun onSubtitleData(
        mediaItem: MediaItem,
        track: SessionPlayer.TrackInfo,
        subtitleData: SubtitleData
      ) {
      }

      override fun onTimedMetadata(mediaItem: MediaItem?, timedMetaData: TimedMetaData?) {
        log.warn("onTimedMetadata() :$timedMetaData")
      }

      override fun onMediaItemStartedAsNext(mediaItem: MediaItem?) {
        log.warn("onMediaItemStartedAsNext")
      }

      override fun onMediaItemEnded(mediaItem: MediaItem?) {
        log.warn("onMediaItemEnded")
      }

      override fun onLoop(mediaItem: MediaItem?) {
        log.warn("onLoop")
      }

      override fun onMediaTimeDiscontinuity(
        mediaItem: MediaItem?,
        mediaTimestamp: MediaTimestamp?
      ) {
        log.warn("onMediaTimeDiscontinuity")
      }

      override fun onPlaybackEnded(mediaItem: MediaItem?) {
        log.warn("onPlaybackEnded")
      }

      override fun onError(mediaItem: MediaItem?, what: Int) {
        log.warn("onError")
      }

    }
    ExoPlayerWrapper(context, listener, Looper.getMainLooper()).also {
      log.warn("calling reset")
      it.reset()
      it.prepare()
    }
  }

  override fun play(url: String) {
    log.info("play() $url")
    player.setMediaItem(
      UriMediaItem.Builder(url.toUri())
        .setMetadata(
          MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, url)
            .putString(MediaMetadata.METADATA_KEY_MEDIA_URI, url)
            .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, "Display Title")
            .putLong(MediaMetadata.METADATA_KEY_PLAYABLE, 1)
            .build()
        )
        .build()
    )
    player.play()

  }

  override fun togglePause() {
    if (player.state == MediaPlayer2.PLAYER_STATE_PLAYING) {
      player.pause()
    } else {
      player.play()
    }
  }
}


class SimpleExoPlayerTest(context: Context) : PlayerTest(context) {

  val player: SimpleExoPlayer by lazy {
    createPlayer()
  }

  val userAgent = Util.getUserAgent(context, "test1").also {
    log.trace("useragent: $it")
  }

  val dataSourceFactory = DefaultDataSourceFactory(context, userAgent, null)

  fun createPlayer() =
    SimpleExoPlayer.Builder(context, DefaultRenderersFactory(context).also {
      it.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
    })
      .setUseLazyPreparation(true)
      .setTrackSelector(DefaultTrackSelector(context))
      .build().also {

        it.setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build(), true
        )
        it.addAnalyticsListener(object : StatsListener {

          override fun onTracksChanged(eventTime: AnalyticsListener.EventTime, trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
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
            log.warn("onMetadata() $metadata")
          }
        })
      }

  override fun play(url: String) {
    log.info("play() $url")

    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
      .createMediaSource(url.toUri())

    log.debug("calling prepare..")
    player.playWhenReady = true
    player.prepare(mediaSource)
  }

  override fun togglePause() {
    if (player.isPlaying) {
      player.playWhenReady = false
    } else {
      player.playWhenReady = true
    }
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(PlayerTest::class.java)
