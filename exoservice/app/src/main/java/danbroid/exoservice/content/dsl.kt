package danbroid.exoservice.content

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import danbroid.media.domain.*
import danbroid.util.resource.toResourceURI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.reflect.KProperty

@DslMarker
annotation class MediaItemDSL

open class MediaItemBuilder(val context: Context) {


  @MediaItemDSL
  var mediaID: String? = null

  @MediaItemDSL
  var title: String? = null

  @StringRes
  var titleID: Int = 0
    set(value) {
      field = value
      title = context.getString(value)
    }

  @MediaItemDSL
  var subtitle: String = ""

  @StringRes
  var subtitleID: Int = 0
    set(value) {
      field = value
      subtitle = context.getString(value)
    }

  @MediaItemDSL
  var imageURI: String? = null

  @MediaItemDSL
  @DrawableRes
  var imageID: Int = 0
    set(value) {
      field = value
      imageURI = value.toResourceURI(context).toString()
    }

  var childBuilders: MutableList<MediaItemBuilder>? = null

  protected var flags: Int = FLAG_DEFAULTS

  @MediaItemDSL
  var liveItem: Flow<MediaItem>? = null

  @MediaItemDSL
  var liveChildren: Flow<List<MediaItem>>? = null

  @MediaItemDSL
  var isBrowsable: Boolean by FlagMask(FLAG_BROWSABLE)
  @MediaItemDSL
  var isPlayable: Boolean by FlagMask(FLAG_PLAYABLE)
  @MediaItemDSL
  var isHidden: Boolean by FlagMask(FLAG_HIDDEN)
  @MediaItemDSL
  var isEditable: Boolean by FlagMask(FLAG_EDITABLE)

  @MediaItemDSL
  var removeItem: ((MediaItem) -> Unit)? = null

  @MediaItemDSL
  var insertItem: ((MediaItem, Int) -> Unit)? = null

  private inner class FlagMask(val flagMask: Int) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
      return (flags and flagMask) == flagMask
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
      flags = if (value) flags.or(flagMask) else flags.and(flagMask.inv())
    }
  }

  @MediaItemDSL
  var uri: String? = null

  fun addChild(child: MediaItemBuilder) {
    (childBuilders ?: mutableListOf<MediaItemBuilder>().also {
      childBuilders = it
    }).also {

      if (child.mediaID == null)
        child.mediaID =
          if (mediaID?.endsWith("/") == true) "$mediaID${it.size}" else "$mediaID/${it.size}"


      flags = flags.or(FLAG_BROWSABLE)
      it.add(child)
    }
  }

  fun find(mediaID: String): MediaItemBuilder? {
    if (this.mediaID == mediaID) return this
    childBuilders?.forEach {
      it.find(mediaID)?.also {
        return it
      }
    }

    return null
  }

  fun buildItem() =
    MediaItem(
      mediaID!!,
      title!!,
      subtitle,
      imageURI = imageURI,
      flags = flags,
      uri = uri
    )

  fun buildChildren(): List<MediaItem>? = childBuilders?.map { it.buildItem() }


  open fun loadItem(): Flow<MediaItem> =
    liveItem ?: flowOf(buildItem())

  open fun loadChildren(): Flow<List<MediaItem>> =
    liveChildren ?: flowOf(buildChildren() ?: emptyList())


  @MediaItemDSL
  fun menu(block: MediaItemBuilder.() -> Unit) = menuItem(MediaItemBuilder(context), block)


  fun <B : MediaItemBuilder> menuItem(builder: B, block: B.() -> Unit) {
    builder.apply(block)
    addChild(builder as MediaItemBuilder)
  }

}

fun rootMenu(
  context: Context,
  builder: MediaItemBuilder = MediaItemBuilder(context),
  block: MediaItemBuilder.() -> Unit
) = builder.apply(block)






