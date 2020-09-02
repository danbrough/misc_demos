package danbroid.media.util

import android.content.Context
import danbroid.media.domain.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.CacheControl
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class MediaItemUtils(val context: Context) {

  @Throws(IOException::class)
  suspend fun postProcess(mediaItem: MediaItem): String? {
    log.trace("postProcess() ${mediaItem.mediaID}:${mediaItem.uri}")

    val uri = mediaItem.uri ?: return null
    val extn = uri.substringAfterLast('.').toLowerCase(Locale.getDefault())

    if (extn == "pls" || extn == "m3u") {
      return coroutineScope {
        async(Dispatchers.Default) {
          processPlaylistURL(uri)
        }
      }.await()
    } else return null
  }

  @Throws(IOException::class)
  private fun processPlaylistURL(
    url: String,
    maxStale: Int = 60 * 60 * 12
  ): String? {
    log.trace("processUrl() $url")

    context.httpSupport.cacheRequest(
      url,
      CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build()
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
  }

}

private val log = org.slf4j.LoggerFactory.getLogger(MediaItemUtils::class.java)

