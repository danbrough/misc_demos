package danbroid.media.service

import android.content.Context
import androidx.media2.common.SessionPlayer
import com.google.android.exoplayer2.Player
import danbroid.media.service.util.httpSupport
import okhttp3.CacheControl
import java.util.concurrent.TimeUnit


suspend fun parsePlaylistURL(context: Context, url: String): String? =
    context.httpSupport.requestString(url, CacheControl.Builder().maxStale(1, TimeUnit.DAYS).build()).let {
      it.lines().firstNotNullOfOrNull { line ->
        val i = line.indexOf('=');
        if (line.startsWith("File") && i > 0) {
          line.substring(i + 1).trim()
        } else if (line.startsWith("http")) {
          line.trimEnd()
        } else null
      }
    }

/*@Throws(IOException::class)
private fun processPlaylistURL(
    url: String,
    maxStale: Int = 60 * 60 * 12
): String? {
  // log.trace("processUrl() $url")

  context.httpSupport.cacheRequest(
      url,
      CacheControl.Builder().maxStale(1, TimeUnit.DAYS).build()
  ).use {
    it.body?.string()?.lines()?.forEach { line ->
      val i = line.indexOf('=');
      if (line.startsWith("File") && i > 0) {
        return line.substring(i + 1).trim()
      } else if (line.startsWith("http")) {
        return line.trimEnd()
      }
    }
  }
  throw IOException("Failed to parse playlist url: $url")
}*/

@SessionPlayer.PlayerState
val Int.playerState: String
  get() = when (this) {
    SessionPlayer.PLAYER_STATE_IDLE -> "PLAYER_STATE_IDLE"
    SessionPlayer.PLAYER_STATE_PAUSED -> "PLAYER_STATE_PAUSED"
    SessionPlayer.PLAYER_STATE_PLAYING -> "PLAYER_STATE_PLAYING"
    SessionPlayer.PLAYER_STATE_ERROR -> "PLAYER_STATE_ERROR"
    else -> "ERROR_INVALID_PLAYER_STATE: $this"
  }


@Player.State
val Int.exoPlayerState: String
  get() = when (this) {
    Player.STATE_IDLE -> "STATE_IDLE"
    Player.STATE_BUFFERING -> "STATE_BUFFERING"
    Player.STATE_ENDED -> "STATE_ENDED"
    Player.STATE_READY -> "STATE_READY"
    else -> "ERROR_INVALID_STATE: $this"
  }

@SessionPlayer.BuffState
val Int.buffState: String
  get() = when (this) {
    SessionPlayer.BUFFERING_STATE_UNKNOWN -> "BUFFERING_STATE_UNKNOWN"
    SessionPlayer.BUFFERING_STATE_BUFFERING_AND_PLAYABLE -> "BUFFERING_STATE_BUFFERING_AND_PLAYABLE"
    SessionPlayer.BUFFERING_STATE_BUFFERING_AND_STARVED -> "BUFFERING_STATE_BUFFERING_AND_STARVED"
    SessionPlayer.BUFFERING_STATE_COMPLETE -> "BUFFERING_STATE_COMPLETE"
    else -> "ERROR_INVALID_BUFF_STATE: $this"
  }

/*
  int PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST = 1;
  */
/** Playback has been paused because of a loss of audio focus. *//*

  int PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS = 2;
  */
/** Playback has been paused to avoid becoming noisy. *//*

  int PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY = 3;
  */
/** Playback has been started or paused because of a remote change. *//*

  int PLAY_WHEN_READY_CHANGE_REASON_REMOTE = 4;
  */
/** Playback has been paused at the end of a media item. *//*

  int PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM = 5;
annotation class PlayWhenReadyChangeReason

*/

@Player.PlayWhenReadyChangeReason
val Int.playWhenReadyChangeReason: String
  get() = when (this) {
    Player.PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST -> "PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST"
    Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS -> "PLAY_WHEN_READY_CHANGE_REASON_AUDIO_FOCUS_LOSS"
    Player.PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY -> "PLAY_WHEN_READY_CHANGE_REASON_AUDIO_BECOMING_NOISY"
    Player.PLAY_WHEN_READY_CHANGE_REASON_REMOTE -> "PLAY_WHEN_READY_CHANGE_REASON_REMOTE"
    Player.PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM -> "PLAY_WHEN_READY_CHANGE_REASON_END_OF_MEDIA_ITEM"
    else -> "ERROR: Invalid PlayWhenReadyChangeReason: $this"
  }

