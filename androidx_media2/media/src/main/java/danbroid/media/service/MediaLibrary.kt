package danbroid.media.service

import androidx.media2.common.MediaItem

interface MediaLibrary {
  suspend fun loadItem(mediaID: String): MediaItem?
}

private val log = danbroid.logging.getLog(MediaLibrary::class)


open class GenericMediaLibrary() : MediaLibrary {

  private val libraries = mutableListOf<MediaLibrary>()

  fun register(vararg libs: MediaLibrary) {
    libraries.addAll(libs)
  }

  override suspend fun loadItem(mediaID: String): MediaItem? =
      libraries.firstNotNullOfOrNull {
        it.loadItem(mediaID)
      }


}