package danbroid.media.service

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media2.common.MediaMetadata
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.icy.IcyHeaders
import com.google.android.exoplayer2.metadata.icy.IcyInfo

data class TrackMetadata(
    var id: String,
    var title: String = "Untitled",
    var subtitle: String = "",
    var imageURI: String? = null,
    var bitrate: Int = -1
) {
  companion object {
    const val MEDIA_METADATA_KEY_BITRATE =
        "danbroid.media.service.TrackMetadata.MEDIA_METADATA_KEY_BITRATE"
    const val MEDIA_METADATA_KEY_CACHED_ICON =
        "danbroid.media.service.TrackMetadata.MEDIA_METADATA_KEY_CACHED_ICON"
    const val MEDIA_METADATA_KEY_LIGHT_COLOR =
        "danbroid.media.service.TrackMetadata.MEDIA_METADATA_KEY_LIGHT_COLOR"
    const val MEDIA_METADATA_KEY_DARK_COLOR =
        "danbroid.media.service.TrackMetadata.MEDIA_METADATA_KEY_DARK_COLOR"
    const val MEDIA_METADATA_KEY_LIGHT_MUTED_COLOR =
        "danbroid.media.service.TrackMetadata.MEDIA_METADATA_KEY_LIGHT_MUTED_COLOR"
    const val MEDIA_METADATA_KEY_DARK_MUTED_COLOR =
        "danbroid.media.service.TrackMetadata.MEDIA_METADATA_KEY_DARK_MUTED_COLOR"
    const val MEDIA_METADATA_KEY_DOMINANT_COLOR =
        "danbroid.media.service.TrackMetadata.MEDIA_METADATA_KEY_DOMINANT_COLOR"
  }

  constructor(md: MediaMetadata) : this(
      md.getString(MediaMetadata.METADATA_KEY_MEDIA_ID)!!
  ) {
    title = md.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)?.toString() ?: "Untitled"
    subtitle = md.getText(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)?.toString() ?: ""
    bitrate = md.extras?.getInt(MEDIA_METADATA_KEY_BITRATE) ?: -1
    imageURI = md.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI)
  }
}


fun TrackMetadata.toMediaMetadata(): MediaMetadata.Builder = MediaMetadata.Builder()
    .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, id)
    .putString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE, title)
    .putString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE, subtitle)
    .putLong(MediaMetadata.METADATA_KEY_PLAYABLE, 1)
    .putString(MediaMetadata.METADATA_KEY_ARTIST, subtitle)
    .setExtras(bundleOf())

    .also { builder ->

      if (imageURI != null) builder.putString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI, imageURI)

      var _bundle: Bundle? = null
      val bundle: () -> Bundle = {
        _bundle ?: Bundle().also {
          _bundle = it
          builder.setExtras(it)
        }
      }
      if (bitrate != -1)
        bundle().putInt(TrackMetadata.MEDIA_METADATA_KEY_BITRATE, bitrate)

    }


private fun Metadata.parseMetadata(trackMD: TrackMetadata): TrackMetadata {
  (0 until this.length()).forEach {
    val entry = get(it)
    when (entry) {
      is IcyInfo -> {
        entry.title.also {
          if (!it.isNullOrEmpty())
            trackMD.subtitle = it
        }
      }
      is IcyHeaders -> {
        entry.name.also {
          if (!it.isNullOrEmpty())
            trackMD.subtitle = it
        }
        entry.bitrate.also {
          if (it != -1)
            trackMD.bitrate = it
        }
      }
    }
  }
  return trackMD
}

fun Metadata.toTrackMetadata(md: MediaMetadata) = parseMetadata(TrackMetadata(md))