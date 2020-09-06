package danbroid.demo.media2.media

import android.annotation.SuppressLint
import android.view.Surface
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.decoder.DecoderCounters
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import java.io.IOException

@SuppressLint("RestrictedApi")
interface StatsListener : AnalyticsListener {

  override fun onPlayerStateChanged(
    eventTime: AnalyticsListener.EventTime,
    playWhenReady: Boolean,
    playbackState: Int
  ) {
  }

  override fun onTimelineChanged(eventTime: AnalyticsListener.EventTime, reason: Int) {

  }

  override fun onPositionDiscontinuity(eventTime: AnalyticsListener.EventTime, reason: Int) {

  }

  override fun onSeekStarted(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onSeekProcessed(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onPlaybackParametersChanged(
    eventTime: AnalyticsListener.EventTime,
    playbackParameters: PlaybackParameters
  ) {

  }

  override fun onRepeatModeChanged(eventTime: AnalyticsListener.EventTime, repeatMode: Int) {

  }

  override fun onShuffleModeChanged(
    eventTime: AnalyticsListener.EventTime,
    shuffleModeEnabled: Boolean
  ) {

  }

  override fun onLoadingChanged(eventTime: AnalyticsListener.EventTime, isLoading: Boolean) {

  }

  override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: ExoPlaybackException) {

  }

  override fun onTracksChanged(
    eventTime: AnalyticsListener.EventTime,
    trackGroups: TrackGroupArray,
    trackSelections: TrackSelectionArray
  ) {

  }

  override fun onLoadStarted(
    eventTime: AnalyticsListener.EventTime,
    loadEventInfo: MediaSourceEventListener.LoadEventInfo,
    mediaLoadData: MediaSourceEventListener.MediaLoadData
  ) {

  }

  override fun onLoadCompleted(
    eventTime: AnalyticsListener.EventTime,
    loadEventInfo: MediaSourceEventListener.LoadEventInfo,
    mediaLoadData: MediaSourceEventListener.MediaLoadData
  ) {

  }

  override fun onLoadCanceled(
    eventTime: AnalyticsListener.EventTime,
    loadEventInfo: MediaSourceEventListener.LoadEventInfo,
    mediaLoadData: MediaSourceEventListener.MediaLoadData
  ) {

  }

  override fun onLoadError(
    eventTime: AnalyticsListener.EventTime,
    loadEventInfo: MediaSourceEventListener.LoadEventInfo,
    mediaLoadData: MediaSourceEventListener.MediaLoadData,
    error: IOException,
    wasCanceled: Boolean
  ) {

  }

  override fun onDownstreamFormatChanged(
    eventTime: AnalyticsListener.EventTime,
    mediaLoadData: MediaSourceEventListener.MediaLoadData
  ) {

  }

  override fun onUpstreamDiscarded(
    eventTime: AnalyticsListener.EventTime,
    mediaLoadData: MediaSourceEventListener.MediaLoadData
  ) {

  }

  override fun onMediaPeriodCreated(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onMediaPeriodReleased(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onReadingStarted(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onBandwidthEstimate(
    eventTime: AnalyticsListener.EventTime,
    totalLoadTimeMs: Int,
    totalBytesLoaded: Long,
    bitrateEstimate: Long
  ) {

  }

  override fun onSurfaceSizeChanged(
    eventTime: AnalyticsListener.EventTime,
    width: Int,
    height: Int
  ) {

  }

  override fun onMetadata(eventTime: AnalyticsListener.EventTime, metadata: Metadata) {

  }

  override fun onDecoderEnabled(
    eventTime: AnalyticsListener.EventTime,
    trackType: Int,
    decoderCounters: DecoderCounters
  ) {

  }

  override fun onDecoderInitialized(
    eventTime: AnalyticsListener.EventTime,
    trackType: Int,
    decoderName: String,
    initializationDurationMs: Long
  ) {

  }

  override fun onDecoderInputFormatChanged(
    eventTime: AnalyticsListener.EventTime,
    trackType: Int,
    format: Format
  ) {

  }

  override fun onDecoderDisabled(
    eventTime: AnalyticsListener.EventTime,
    trackType: Int,
    decoderCounters: DecoderCounters
  ) {

  }

  override fun onAudioSessionId(eventTime: AnalyticsListener.EventTime, audioSessionId: Int) {

  }

  override fun onAudioAttributesChanged(
    eventTime: AnalyticsListener.EventTime,
    audioAttributes: AudioAttributes
  ) {

  }

  override fun onVolumeChanged(eventTime: AnalyticsListener.EventTime, volume: Float) {

  }

  override fun onAudioUnderrun(
    eventTime: AnalyticsListener.EventTime,
    bufferSize: Int,
    bufferSizeMs: Long,
    elapsedSinceLastFeedMs: Long
  ) {

  }

  override fun onDroppedVideoFrames(
    eventTime: AnalyticsListener.EventTime,
    droppedFrames: Int,
    elapsedMs: Long
  ) {

  }

  override fun onVideoSizeChanged(
    eventTime: AnalyticsListener.EventTime,
    width: Int,
    height: Int,
    unappliedRotationDegrees: Int,
    pixelWidthHeightRatio: Float
  ) {

  }

  override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime, surface: Surface?) {

  }

  override fun onDrmSessionAcquired(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onDrmKeysLoaded(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onDrmSessionManagerError(eventTime: AnalyticsListener.EventTime, error: Exception) {

  }

  override fun onDrmKeysRestored(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onDrmKeysRemoved(eventTime: AnalyticsListener.EventTime) {

  }

  override fun onDrmSessionReleased(eventTime: AnalyticsListener.EventTime) {

  }

}