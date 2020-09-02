package danbroid.media.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlin.reflect.KProperty

const val FLAG_DEFAULTS = 0
const val FLAG_BROWSABLE = 1 shl 0
const val FLAG_PLAYABLE = 1 shl 1
const val FLAG_HIDDEN = 1 shl 2
const val FLAG_EDITABLE = 1 shl 3

const val TABLE_MEDIA_ITEM = "media_item"


@Parcelize
@Entity(
  tableName = TABLE_MEDIA_ITEM,
  primaryKeys = ["mediaID", "parentID"]
)
data class MediaItem(
  @Expose
  var mediaID: String,

  @Expose
  var title: String,

  @Expose
  var subtitle: String,

  @Expose
  var imageURI: String? = null,

  @Expose
  var flags: Int = FLAG_DEFAULTS,
  var uri: String? = null,

  @Expose
  var website: String? = null,

  @Expose
  var parentID: String = "",

  @Expose
  var modified: Long = System.currentTimeMillis()

) : Parcelable {

  @IgnoredOnParcel
  @delegate:Ignore
  var isBrowsable: Boolean by FlagMask(FLAG_BROWSABLE)

  @IgnoredOnParcel
  @delegate:Ignore
  var isPlayable: Boolean by FlagMask(FLAG_PLAYABLE)

  @IgnoredOnParcel
  @delegate:Ignore
  var isHidden: Boolean by FlagMask(FLAG_HIDDEN)

  @IgnoredOnParcel
  @delegate:Ignore
  var isEditable: Boolean by FlagMask(FLAG_EDITABLE)

  private inner class FlagMask(val flagMask: Int) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
      return (flags and flagMask) == flagMask
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
      flags = if (value) flags or flagMask else flags and flagMask.inv()
    }
  }

  @IgnoredOnParcel
  @Ignore
  var extras: Any? = null
}

private val log = org.slf4j.LoggerFactory.getLogger(MediaItem::class.java)

