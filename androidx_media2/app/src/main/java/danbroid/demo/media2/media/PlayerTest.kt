package danbroid.demo.media2.media

import android.content.Context
import androidx.core.net.toUri
import androidx.media2.player.StatsListener
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class PlayerTest(val context: Context) {

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
          override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
            log.warn("onMetadata() $metadata")
          }
        })
      }

  fun play(url: String) {
    log.info("play() $url")

    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
      .createMediaSource(url.toUri())

    log.debug("calling prepare..")
    player.playWhenReady = true
    player.prepare(mediaSource)
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(PlayerTest::class.java)
