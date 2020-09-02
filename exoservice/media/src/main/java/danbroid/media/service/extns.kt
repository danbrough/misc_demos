package danbroid.media.service

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import danbroid.media.domain.MediaItem

fun Int.toPlaybackStateName(): String {
  @Player.State
  val state: Int = this
  return when (state) {
    Player.STATE_BUFFERING -> "STATE_BUFFERING"
    Player.STATE_ENDED -> "STATE_ENDED"
    Player.STATE_IDLE -> "STATE_IDLE"
    Player.STATE_READY -> "STATE_READY"
    else -> "Invalid state: $state"
  }
}

fun Int.toTimelineChangeReason(): String {
  @Player.TimelineChangeReason
  val reason: Int = this
  return when (reason) {
    Player.TIMELINE_CHANGE_REASON_DYNAMIC -> "TIMELINE_CHANGE_REASON_DYNAMIC"
    Player.TIMELINE_CHANGE_REASON_PREPARED -> "TIMELINE_CHANGE_REASON_PREPARED"
    Player.TIMELINE_CHANGE_REASON_RESET -> "TIMELINE_CHANGE_REASON_RESET"
    else -> "Invalid TimelineChangeReason: $reason"
  }
}

fun Int.toPlaybackSuppressionReason(): String {
  @Player.PlaybackSuppressionReason
  val reason: Int = this
  return when (reason) {
    Player.PLAYBACK_SUPPRESSION_REASON_NONE -> "PLAYBACK_SUPPRESSION_REASON_NONE"
    Player.PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS -> "PLAYBACK_SUPPRESSION_REASON_TRANSIENT_AUDIO_FOCUS_LOSS"
    else -> "Invalid PlaybackSuppressionReason: $reason"
  }
}

