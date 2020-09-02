package danbroid.demo.media2.media

import androidx.media2.common.SessionPlayer
import androidx.media2.common.SessionPlayer.*


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
