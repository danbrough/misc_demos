package danbroid.media.service

import androidx.media2.common.SessionPlayer
import androidx.media2.common.SessionPlayer.*
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.icy.IcyInfo


@SessionPlayer.PlayerState
val Int.playerState: String
  get() = when (this) {
    PLAYER_STATE_IDLE -> "PLAYER_STATE_IDLE"
    PLAYER_STATE_PAUSED -> "PLAYER_STATE_PAUSED"
    PLAYER_STATE_PLAYING -> "PLAYER_STATE_PLAYING"
    PLAYER_STATE_ERROR -> "PLAYER_STATE_ERROR"
    else -> "ERROR_INVALID_PLAYER_STATE: $this"
  }


@SessionPlayer.BuffState
val Int.buffState: String
  get() = when (this) {
    BUFFERING_STATE_UNKNOWN -> "BUFFERING_STATE_UNKNOWN"
    BUFFERING_STATE_BUFFERING_AND_PLAYABLE -> "BUFFERING_STATE_BUFFERING_AND_PLAYABLE"
    BUFFERING_STATE_BUFFERING_AND_STARVED -> "BUFFERING_STATE_BUFFERING_AND_STARVED"
    BUFFERING_STATE_COMPLETE -> "BUFFERING_STATE_COMPLETE"
    else -> "ERROR_INVALID_BUFF_STATE: $this"
  }

