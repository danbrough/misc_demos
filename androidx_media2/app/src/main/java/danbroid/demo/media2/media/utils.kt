package danbroid.demo.media2.media

import androidx.media2.common.SessionPlayer
import androidx.media2.common.SessionPlayer.*


/*
    /**
     * State when the player is idle, and needs configuration to start playback.
     */
    public static final int PLAYER_STATE_IDLE = 0;

    /**
     * State when the player's playback is paused
     */
    public static final int PLAYER_STATE_PAUSED = 1;

    /**
     * State when the player's playback is ongoing
     */
    public static final int PLAYER_STATE_PLAYING = 2;

    /**
     * State when the player is in error state and cannot be recovered self.
     */
    public static final int PLAYER_STATE_ERROR = 3;
 */
@SessionPlayer.PlayerState
val Int.playerState: String
  get() = when (this) {
    PLAYER_STATE_IDLE -> "PLAYER_STATE_IDLE"
    PLAYER_STATE_PAUSED -> "PLAYER_STATE_PAUSED"
    PLAYER_STATE_PLAYING -> "PLAYER_STATE_PLAYING"
    PLAYER_STATE_ERROR -> "PLAYER_STATE_ERROR"
    else -> ""
  }


@SessionPlayer.BuffState
val Int.buffState: String
  get() = when (this) {
    BUFFERING_STATE_UNKNOWN -> "BUFFERING_STATE_UNKNOWN"
    BUFFERING_STATE_BUFFERING_AND_PLAYABLE -> "BUFFERING_STATE_BUFFERING_AND_PLAYABLE"
    BUFFERING_STATE_BUFFERING_AND_STARVED -> "BUFFERING_STATE_BUFFERING_AND_STARVED"
    BUFFERING_STATE_COMPLETE -> "BUFFERING_STATE_COMPLETE"
    else -> ""
  }
