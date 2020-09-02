package danbroid.exoservice.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import danbroid.exoservice.content.rootContent
import danbroid.media.domain.MediaItem
import danbroid.media.messages.*
import danbroid.media.util.MediaItemUtils
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform

class AudioPlayer(val context: Context) {

  private val mediaItemUtils = MediaItemUtils(context)

  val serviceConnection = AudioServiceConnection(context).also {
    it.connect()
  }

  val playItem: LiveData<MediaItem?> =
    serviceConnection.messages.transform {
      if (it is PLAYER_ITEM) emit(it.item)
    }.asLiveData()

  val loading: LiveData<Boolean> =
    serviceConnection.messages.transform {
      when {
        it is LoadingEvent.STARTED -> emit(true)
        it is LoadingEvent.STOPPED -> emit(false)
      }
    }.asLiveData()

  @Suppress("UNCHECKED_CAST")
  val playerState: LiveData<PLAYER_STATE_CHANGE> =
    serviceConnection.messages.filter {
      it is PLAYER_STATE_CHANGE
    }.asLiveData() as LiveData<PLAYER_STATE_CHANGE>


  @Suppress("UNCHECKED_CAST")
  val playbackQueue: LiveData<PLAYLIST> =
    serviceConnection.messages.filter {
      it is PLAYLIST
    }.asLiveData() as LiveData<PLAYLIST>


  fun close() {
    log.info("close()")
    serviceConnection.disconnect()
  }

  suspend fun play(item: MediaItem) {
    log.warn("play() $item")

    val builder =
      rootContent(context).find(item.mediaID)


    builder?.liveItem?.also {

      it.take(1).collect { mediaItem ->
        log.warn("COLLECTED $mediaItem")
        mediaItemUtils.postProcess(mediaItem)?.also {
          log.warn("RESOLVED URI: $it")
          mediaItem.uri = it
        }
        serviceConnection.sendMessage(PlayerAction.PLAY(mediaItem))
      }
      return
    }

    mediaItemUtils.postProcess(item)?.also {
      log.warn("RESOLVED URI: $it")
      item.uri = it
    }
    serviceConnection.sendMessage(PlayerAction.PLAY(item))

  }

  fun skipPrev() = serviceConnection.sendMessage(PlayerAction.SKIP_PREV)
  fun skipNext() = serviceConnection.sendMessage(PlayerAction.SKIP_NEXT)

  fun togglePause() = serviceConnection.sendMessage(PlayerAction.TOGGLE_PAUSE)
  fun emptyQueue() = serviceConnection.sendMessage(PlayerAction.EMPTY_QUEUE)
  fun stop() = serviceConnection.sendMessage(PlayerAction.STOP)

  fun removeFromQueue(item: MediaItem) =
    serviceConnection.sendMessage(PlaylistAction.REMOVE_FROM_PLAYLIST(item))

  fun insertIntoQueue(item: MediaItem, index: Int) =
    serviceConnection.sendMessage(PlaylistAction.INSERT_INTO_PLAYLIST(item, index))

}

private val log = org.slf4j.LoggerFactory.getLogger(AudioPlayer::class.java)

