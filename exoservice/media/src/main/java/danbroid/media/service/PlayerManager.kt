package danbroid.media.service

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.icy.IcyInfo
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import danbroid.media.domain.MediaItem
import danbroid.media.messages.*

val mediaSource = ConcatenatingMediaSource()

class PlayerManager(val service: AudioService) {


  val context: Context = service
  private val handler = Handler()


  private val userAgent = Util.getUserAgent(service, "exodemo")

  private val dataSourceFactory = DefaultDataSourceFactory(service, userAgent, null)
  private val extractorsFactory = DefaultExtractorsFactory()


  private val notificationMananger: PlayerNotificationManager by lazy {
    createNotificationManager(
      service,
      notificationListener = notificationListener
    )
  }


  val player: ExoPlayer
    get() = getOrCreatePlayer()

  private var _player: ExoPlayer? = null

  private fun getOrCreatePlayer(): ExoPlayer = _player ?: createPlayer().also {
    _player = it
  }

  var foreground = false

  val notificationListener = object : PlayerNotificationManager.NotificationListener {

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
      log.warn("onNotificationCancelled() byUser:$dismissedByUser")
      if (dismissedByUser)
        stop()
      else {
        if (foreground) {
          log.warn("stopping foreground ..")
          service.stopForeground(true)
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
          ContextCompat.startForegroundService(
            service.applicationContext,
            Intent(service.applicationContext, service.javaClass)
          )
          service.startForeground(notificationId, notification)
          foreground = true
        }
      } else {
        if (foreground) {
          log.warn("stopping foreground ..")
          service.stopForeground(false)
          foreground = false
        }
      }
    }
  }

  private fun createPlayer(): ExoPlayer {
    /*.also {

           it.addEventListener(handler, object : MediaSourceEventListener {

             override fun onLoadStarted(
               windowIndex: Int,
               mediaPeriodId: MediaSource.MediaPeriodId?,
               loadEventInfo: LoadEventInfo?,
               mediaLoadData: MediaLoadData?
             ) {
               log.error("onLoadStarted() windowindex:$windowIndex")
             }

             override fun onLoadCompleted(
               windowIndex: Int,
               mediaPeriodId: MediaSource.MediaPeriodId?,
               loadEventInfo: LoadEventInfo?,
               mediaLoadData: MediaLoadData?
             ) {
               log.error("onLoadCompleted() windowindex:$windowIndex")
             }

             override fun onMediaPeriodCreated(
               windowIndex: Int,
               mediaPeriodId: MediaSource.MediaPeriodId?
             ) {
               log.error("onMediaPeriodCreated() windowIndex:$windowIndex.")
             }

             override fun onMediaPeriodReleased(
               windowIndex: Int,
               mediaPeriodId: MediaSource.MediaPeriodId?
             ) {
               log.error("onMediaPeriodReleased() windowIndex:$windowIndex.")
             }
           })
         }*/

    return SimpleExoPlayer.Builder(
      context,
      DefaultRenderersFactory(context).also {
        it.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
      })
      //.setBandwidthMeter(bandwidthMeter)
      //.setLoadControl(loadControl)
      .setTrackSelector(DefaultTrackSelector(context))
      .setUseLazyPreparation(true)
      .build().also {
        it.setMediaItem(mediaSource!!)
        it.addListener(PlayerListener())
        it.analyticsCollector.addListener(StatsListener())

        it.setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build(), true
        )
      }
  }


  inner class StatsListener : AnalyticsListener {

    override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {
      log.warn("onMetadata() $metadata class:${metadata.javaClass} ")

      for (n in 0 until metadata.length()) {
        val md = metadata[n]
        if (md is IcyInfo) {
          log.trace("MD TITLE: [${md.title}]  URL: [${md.url}]")
          if (!md.title.isNullOrEmpty()) {
            player.mediaItem?.also {
              it.subtitle = md.title.toString()
              log.error("UPDATED MEDIA ITEM")
              service.broadcastMessage(PLAYER_ITEM(it))
            }
            notificationMananger.invalidate()
          }
        }
      }
    }


/*    override fun onIsPlayingChanged(eventTime: AnalyticsListener.EventTime, isPlaying: Boolean) {
      log.warn("onIsPlayingChanged()  isPlaying:$isPlaying")
    }*/

    override fun onBandwidthEstimate(
      eventTime: AnalyticsListener.EventTime,
      totalLoadTimeMs: Int,
      totalBytesLoaded: Long,
      bitrateEstimate: Long
    ) {
      log.warn("onBandiwdthEstimate: totalLoadTimeMs:$totalLoadTimeMs totalBytesLoaded:$totalBytesLoaded bitrateEstimate:$bitrateEstimate")
    }
  }

  private var currentItem: MediaItem? = null

  private fun updateCurrentItem() {
    val item = player.mediaItem
    if (item != currentItem) {
      currentItem = item
      log.info("CURRENT ITEM IS $item")
      service.broadcastMessage(PLAYER_ITEM(item))
    }
  }

  inner class PlayerListener : Player.EventListener {

    override fun onTimelineChanged(timeline: Timeline, reason: Int) {
      log.warn("onTimelineChanged() windowCount:${timeline.windowCount} reason:${reason.toTimelineChangeReason()} item:${player.mediaItem?.title}")
      /*     sendQueue()
           updateCurrentItem()
           sendPlayerState()*/
    }


    override fun onTracksChanged(
      trackGroups: TrackGroupArray,
      trackSelections: TrackSelectionArray
    ) {
      /*    log.info("onTracksChanged() groupCount:${trackGroups.length}  selectionCount:${trackSelections.length}")
          for (n in 0..trackGroups.length - 1) {
            log.info("TRACKGROUP $n: length: ${trackGroups[n].length}")
          }*/


    }

    override fun onPositionDiscontinuity(reason: Int) {
      log.trace("onPositionDiscontinuity() reason:${reason.toTimelineChangeReason()} windowIndex: ${player.currentWindowIndex} ${player.mediaItem?.title}")
      updateCurrentItem()
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
      //log.trace("onPlaybackParametersChanged() $playbackParameters")

    }

    override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
      //log.trace("onPlaybackSuppressionReasonChanged() playbackSuppressionReason:$playbackSuppressionReason")
    }

    override fun onPlayerError(error: ExoPlaybackException) {
      log.error("onPlayerError() $error", error)
    }

    override fun onLoadingChanged(isLoading: Boolean) {
      log.trace("onLoadingChanged(): $isLoading")
      service.broadcastMessage(if (isLoading) LoadingEvent.STARTED else LoadingEvent.STOPPED)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
      log.trace("onIsPlayingChanged(): $isPlaying")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) =
      sendPlayerState()

  }

  private var lastState: PLAYER_STATE_CHANGE? = null


  private fun sendPlayerState() {
    PLAYER_STATE_CHANGE(
      when (player.playbackState) {
        Player.STATE_IDLE -> PlayerState.IDLE
        Player.STATE_BUFFERING -> PlayerState.BUFFERING
        Player.STATE_READY -> {
          //  service.broadcastMessage(PLAYER_ITEM(player.mediaItem))
          PlayerState.READY
        }
        Player.STATE_ENDED -> PlayerState.ENDED
        else -> throw Exception("Invalid playbackState: ${player.playbackState}")
      },
      player.isPlaying,
      player.hasPrevious(),
      player.hasNext()
    ).also {
      if (lastState != it) {
        lastState = it
        service.broadcastMessage(it)
      }
    }
  }


  private fun sendPlaylist() {
    log.info("sendPlaylist() size: ${mediaSource.size}")

    (0 until mediaSource.size).map {
      mediaSource.getMediaSource(it).tag as MediaItem
    }.also {
      service.broadcastMessage(PLAYLIST(it))
    }

    sendPlayerState()
  }

  private fun play(mediaItem: MediaItem) {
    log.warn("play() $mediaItem currentManifest: ${player.currentManifest}")
    notificationMananger.setPlayer(player)

    for (n in 0 until mediaSource.size) {
      mediaSource.getMediaSource(n).also {
        val item = it.tag as MediaItem
        if (item.mediaID == mediaItem.mediaID) {
          log.error("ALREADY IN QUEUE! .. shall play it instead..")
          player.seekTo(n, 0)
          player.playWhenReady = true
          if (!player.isPlaying) {
            log.error("NOT PLAYING")
            player.prepare()
          }
          return
        }
      }
    }


   // log.trace("creating media source")


    mediaSource.addMediaSource(createMediaSource(mediaItem), handler) {
      sendPlaylist()
      service.broadcastMessage(
        PLAYLIST_EVENT(
          PLAYLIST_EVENT.PlaylistEvent.ADDED_TO_PLAYLIST,
          mediaItem
        )
      )
      if (mediaSource.size == 1) {
      //  log.trace("adding and playing item ..")
        //player.setMediaItem(mediaSource)
        updateCurrentItem()
        player.playWhenReady = true
        player.prepare()
      }
    }


    service.broadcastMessage(
      PLAYLIST_EVENT(
        PLAYLIST_EVENT.PlaylistEvent.ADDED_TO_PLAYLIST,
        mediaItem
      )
    )
  }

  fun createMediaSource(mediaItem: MediaItem): MediaSource {
   // log.info("createMediaSource() $mediaItem uri:${mediaItem.uri}")
    return ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
      .setTag(mediaItem)
      .createMediaSource(mediaItem.uri!!.toUri())
  }

  private fun togglePause() {
    log.info("togglePause() state:${player.playbackState.toPlaybackStateName()} duration:${player.contentDuration} position:${player.contentPosition} bufferedPosition:${player.contentBufferedPosition}")

    if (player.playbackState != Player.STATE_READY && player.playbackState != Player.STATE_BUFFERING) {
      player.playWhenReady = true
      player.seekTo(0)
      if (!player.isPlaying) {
        log.trace("calling prepare as idle")
        player.prepare()
      }
    } else {
      player.playWhenReady = !player.playWhenReady
    }

  }

  private fun skipPrev() {
    if (player.hasPrevious())
      player.seekTo(player.previousWindowIndex, 0)
  }

  private fun skipNext() {

    if (player.hasNext())
      player.seekTo(player.nextWindowIndex, 0)
  }

  fun close() {
    log.info("close()")
    notificationMananger.setPlayer(null)
    _player?.also {
      it.stop(true)
      it.release()
      _player = null
    }
  }

  private fun emptyQueue() {
    log.info("emptyQueue()")
    player.stop(false)
    mediaSource.clear(handler) {
      //player.setMediaItem(mediaSource)
      //notificationMananger.setPlayer(null)
      sendPlaylist()
      service.broadcastMessage(PLAYLIST_EVENT(PLAYLIST_EVENT.PlaylistEvent.CLEARED_PLAYLIST, null))
    }
  }

  private fun stop() {
    player.stop(false)
    //  player.setMediaItem(mediaSource)
  }

  private fun removeFromQueue(item: MediaItem) {
    log.warn("removeFromQueue() $item")
    for (n in 0 until mediaSource.size) {
      (mediaSource.getMediaSource(n).tag as MediaItem).also {
        if (it.mediaID == item.mediaID) {
          log.warn("FOUND IN QUEUE $it")
          if (mediaSource.size == 1) {
            log.error("QUEUE IS EMPTY")
            player.stop(false)
            notificationMananger.setPlayer(null)
          }

          try {
            mediaSource.removeMediaSource(n, handler) {
              sendPlaylist()
              service.broadcastMessage(
                PLAYLIST_EVENT(
                  PLAYLIST_EVENT.PlaylistEvent.REMOVED_FROM_PLAYLIST,
                  item
                )
              )
            }
          } catch (err: Exception) {
            log.error("removeMediaSource error: ${err.message}", err)
          }

          //player.setMediaItem(mediaSource)
          return
        }
      }
    }
  }

  private fun insertIntoQueue(item: MediaItem, index: Int) {
    log.warn("insertIntoQueue() $item index:$index")
    mediaSource.addMediaSource(index, createMediaSource(item), handler) {
      player.setMediaItem(mediaSource)
      sendPlaylist()
      updateCurrentItem()
      service.broadcastMessage(PLAYLIST_EVENT(PLAYLIST_EVENT.PlaylistEvent.ADDED_TO_PLAYLIST, item))
    }

  }

  fun processMessage(message: AppMessage) {
    try {
      when (message) {
        is PlayerAction.PLAY ->
          service.playerManager.play(message.item)

        is PlayerAction.TOGGLE_PAUSE -> togglePause()
        is PlayerAction.SKIP_PREV -> skipPrev()
        is PlayerAction.SKIP_NEXT -> skipNext()
        is PlayerAction.EMPTY_QUEUE -> emptyQueue()
        is PlayerAction.STOP -> stop()
        is PlaylistAction.REMOVE_FROM_PLAYLIST -> removeFromQueue(message.item)
        is PlaylistAction.INSERT_INTO_PLAYLIST -> insertIntoQueue(
          message.item,
          message.index
        )

        else -> log.warn("Unhandled message: $message")
      }
    } catch (err: Exception) {
      log.error("ERROR: ${err.message}", err)
    }
  }


}

val Player.mediaItem: MediaItem?
  get() = if (currentWindowIndex > -1 && currentWindowIndex < mediaSource.size)
    mediaSource.getMediaSource(currentWindowIndex).tag as MediaItem
  else null
/*(this as ExoPlayer).setMediaItem()
return if (currentWindowIndex > -1 && currentWindowIndex < currentTimeline.windowCount)
  currentTimeline.getWindow(currentWindowIndex, Timeline.Window()).tag as MediaItem
else null*/


private val log = org.slf4j.LoggerFactory.getLogger(PlayerManager::class.java)

