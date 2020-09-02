package danbroid.media.messages

import android.os.Message
import android.os.Parcelable
import androidx.core.os.bundleOf
import danbroid.media.BuildConfig
import danbroid.media.domain.MediaItem
import kotlinx.android.parcel.Parcelize
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf


sealed class AppMessage(
  private val arg1: Int = 0, private val arg2: Int = 0
) {

  val type = MESSAGE_TYPES.indexOf(this::class)

  fun toMessage() = Message.obtain(null, type, arg1, arg2).also {
    if (this is Parcelable)
      it.data = bundleOf(MESSAGE_BUNDLE_KEY to this)
  }

  companion object {
    const val MESSAGE_BUNDLE_KEY = "_message"

    @Suppress("UNCHECKED_CAST")
    fun getParcelableMessage(msg: Message): AppMessage? {
      val data = msg.data
      data.classLoader = this@Companion::class.java.classLoader
      return if (data.containsKey(MESSAGE_BUNDLE_KEY)) {
        data[MESSAGE_BUNDLE_KEY] as AppMessage
      } else null
    }
  }

}

private fun addMessageClasses(
  cls: KClass<out AppMessage>,
  classes: MutableList<KClass<out AppMessage>>
) {
  if (cls.isFinal) classes.add(cls)
  if (cls.isSealed) {
    cls.sealedSubclasses.forEach {
      addMessageClasses(it, classes)
    }
  }
}

private fun messageClasses(): List<KClass<out AppMessage>> {
  val classes = mutableListOf<KClass<out AppMessage>>()
  addMessageClasses(
    AppMessage::class,
    classes
  )
  return classes
}


val MESSAGE_TYPES = messageClasses()

fun Message.asAppMessage(): AppMessage {

  val messageType: KClass<out AppMessage> = MESSAGE_TYPES[what]

  val parcelable = messageType.isSubclassOf(Parcelable::class)

  if (BuildConfig.DEBUG)
    log.warn("messageType: ${messageType.simpleName} parcelable:${parcelable}")

  messageType.objectInstance?.let {
    return it
  }

  return AppMessage.getParcelableMessage(this) ?: TODO("Unhandled message type: $messageType")
}

sealed class LoadingEvent : AppMessage() {
  object STARTED : LoadingEvent()
  object STOPPED : LoadingEvent()
}

sealed class RegisterEvent : AppMessage() {
  object REGISTER : RegisterEvent()
  object UNREGISTER : RegisterEvent()
}


sealed class PlaylistAction : AppMessage() {

  @Parcelize
  data class REMOVE_FROM_PLAYLIST(val item: MediaItem) : PlaylistAction(), Parcelable

  @Parcelize
  data class INSERT_INTO_PLAYLIST(val item: MediaItem, val index: Int) : PlaylistAction(),
    Parcelable
}

enum class PlayerState {
  IDLE, BUFFERING, READY, ENDED
}

@Parcelize
data class PLAYER_STATE_CHANGE(
  val state: PlayerState,
  val isPlaying: Boolean,
  val hasPrev: Boolean,
  val hasNext: Boolean
) : AppMessage(), Parcelable


@Parcelize
data class PLAYLIST_EVENT(val eventType: PlaylistEvent, val item: MediaItem?) : AppMessage(),
  Parcelable {
  enum class PlaylistEvent {
    ADDED_TO_PLAYLIST,
    REMOVED_FROM_PLAYLIST,
    CLEARED_PLAYLIST
  }
}


@Parcelize
data class PLAYLIST(val queue: List<MediaItem>) : AppMessage(), Parcelable

@Parcelize
data class PLAYER_ITEM(val item: MediaItem?) : AppMessage(), Parcelable

sealed class PlayerAction : AppMessage() {
  object SKIP_NEXT : PlayerAction()
  object SKIP_PREV : PlayerAction()
  object EMPTY_QUEUE : PlayerAction()
  object STOP : PlayerAction()
  object TOGGLE_PAUSE : PlayerAction()
  @Parcelize
  data class PLAY(val item: MediaItem) : PlayerAction(), Parcelable
}


/*  inline fun <reified T : AppMessage> T.ordinal() =
    T::class.java.superclass?.classes?.indexOfFirst { sub -> sub == this@ordinal::class.java }*/


private val log = LoggerFactory.getLogger(AppMessage::class.java)









