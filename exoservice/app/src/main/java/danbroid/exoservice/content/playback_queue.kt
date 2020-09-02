package danbroid.exoservice.content

import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import danbroid.exoservice.player

const val URI_CONTENT_PLAYBACK_QUEUE = "$URI_CONTENT_PREFIX/playbackQueue"

@MediaItemDSL
fun MediaItemBuilder.playbackQueue(block: MediaItemBuilder.() -> Unit) =
  menu {

    mediaID = URI_CONTENT_PLAYBACK_QUEUE
    title = "Playback Queue"
    isBrowsable = true
    isEditable = true

    liveChildren =
      context.player.playbackQueue.map { it.queue }.asFlow()

    removeItem = context.player::removeFromQueue
    insertItem = context.player::insertIntoQueue


    block()
  }


