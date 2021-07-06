package danbroid.media.service.util

import android.content.Context
import danbroid.media.service.audioServiceConfig
import danbroid.util.misc.SingletonHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class HttpSupport(context: Context) {
  companion object : SingletonHolder<HttpSupport, Context>(::HttpSupport)

  private val okHttp by lazy {
    OkHttpClient.Builder().cache(
        Cache(File(context.cacheDir, "okhttp"), context.audioServiceConfig.HTTP_CACHE_SIZE)).build()
  }

  @Suppress("BlockingMethodInNonBlockingContext")
  suspend fun requestString(url: String, cacheControl: CacheControl? = null, retryWithoutCache: Boolean = false): String = withContext(Dispatchers.IO) {
    Request.Builder().url(url)
        .also {
          if (cacheControl != null) it.cacheControl(cacheControl)
        }
        .build().let {
          okHttp.newCall(it).execute().use {
            if (it.isSuccessful)
              it.body!!.string()
            else {
              if (cacheControl != null && retryWithoutCache) {
                log.info("$url failed. code:${it.code} message:${it.message} retrying without cache")
                requestString(url)
              } else
                throw IOException("$url failed. code:${it.code} message:${it.message}")
            }
          }
        }
  }
}

val Context.httpSupport: HttpSupport
  get() = HttpSupport.getInstance(this)

private val log = danbroid.logging.getLog(HttpSupport::class)
