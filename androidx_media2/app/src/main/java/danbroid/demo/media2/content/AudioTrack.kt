package danbroid.demo.media2.content

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.media2.common.MediaMetadata
import danbroid.media.service.AudioService

data class AudioTrack(
    var id: String,
    var title: String = "Untitled",
    var subtitle: String = "",
    var imageURI: String? = null,
    var bitrate: Int = -1
) {
  constructor(md: MediaMetadata) : this(
      md.getString(MediaMetadata.METADATA_KEY_MEDIA_ID)!!
  ) {
    title = md.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)?.toString() ?: "Untitled"
    subtitle = md.getText(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)?.toString() ?: ""
    bitrate = md.extras?.getInt(AudioService.MEDIA_METADATA_KEY_BITRATE) ?: -1
    imageURI = md.getString(MediaMetadata.METADATA_KEY_DISPLAY_ICON_URI)
  }
}


fun AudioTrack.toMediaMetadata(): MediaMetadata.Builder = MediaMetadata.Builder()
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
        bundle().putInt(AudioService.MEDIA_METADATA_KEY_BITRATE, bitrate)

    }


/*
private fun Metadata.parseMetadata(audioTrackMD: AudioTrack): AudioTrack {
  (0 until this.length()).forEach {
    val entry = get(it)
    when (entry) {
      is IcyInfo -> {
        entry.title.also {
          if (!it.isNullOrEmpty())
            audioTrackMD.subtitle = it
        }
      }
      is IcyHeaders -> {
        entry.name.also {
          if (!it.isNullOrEmpty())
            audioTrackMD.subtitle = it
        }
        entry.bitrate.also {
          if (it != -1)
            audioTrackMD.bitrate = it
        }
      }
    }
  }
  return audioTrackMD
}

fun Metadata.toTrackMetadata(md: MediaMetadata) = parseMetadata(AudioTrack(md))*/
