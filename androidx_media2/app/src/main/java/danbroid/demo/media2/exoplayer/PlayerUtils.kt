package danbroid.demo.media2.exoplayer

import android.annotation.SuppressLint
import androidx.media2.common.SessionPlayer
import androidx.media2.exoplayer.external.SimpleExoPlayer
import androidx.media2.exoplayer.external.analytics.AnalyticsListener
import androidx.media2.exoplayer.external.metadata.Metadata
import androidx.media2.exoplayer.external.source.MediaSourceEventListener
import androidx.media2.player.StatsListener

object  PlayerUtils {
  @SuppressLint("RestrictedApi")
  fun configure(player: SessionPlayer) {
    runCatching {
      val f = player.javaClass.getDeclaredField("mPlayer")
      f.isAccessible = true
      val p = f.get(player)
      log.warn("PLAYER $p  class: ${p.javaClass}")
      val wrapper = p.javaClass.getDeclaredField("mPlayer").let {
        it.isAccessible = true
        it.get(p)
      }
      wrapper.javaClass.getDeclaredField("mPlayer").let {
        it.isAccessible = true
        val exoPlayer = it.get(wrapper) as SimpleExoPlayer
        log.warn("registering listener")

        exoPlayer.addAnalyticsListener(object : StatsListener {

          override fun onLoadStarted(
            eventTime: AnalyticsListener.EventTime,
            loadEventInfo: MediaSourceEventListener.LoadEventInfo,
            mediaLoadData: MediaSourceEventListener.MediaLoadData
          ) {
            log.warn("onLoadStarted() $eventTime $loadEventInfo $mediaLoadData")
          }


          override fun onBandwidthEstimate(
            eventTime: AnalyticsListener.EventTime,
            totalLoadTimeMs: Int,
            totalBytesLoaded: Long,
            bitrateEstimate: Long
          ) {
            log.warn("onBandwidthEstimate() totalBytesLoaded: $totalBytesLoaded bitrateEstimate: $bitrateEstimate")
          }

          override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
            log.warn("metadata: $metadata")
          }
        })
      }


    }?.exceptionOrNull()?.also {
      log.error(it.message, it)
    }

  }
}

private val log = org.slf4j.LoggerFactory.getLogger(PlayerUtils::class.java)
