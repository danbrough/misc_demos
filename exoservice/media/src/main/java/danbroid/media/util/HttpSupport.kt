package danbroid.media.util

import android.content.Context
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class HttpSupport private constructor(context: Context) {


  var cacheDir = File(context.cacheDir, "okhttp")

  val CACHE_SIZE = 1024 * 1024 * 10L

  val cache: Cache by lazy { Cache(cacheDir, CACHE_SIZE) }

  val client = OkHttpClient.Builder().cache(cache).build()

  @Throws(IOException::class)
  fun cacheRequest(
    url: String,
    cacheControlFirst: CacheControl,
    cacheControlSecond: CacheControl? = null
  ): Response {

    if (cacheControlSecond == null) return request(url, cacheControlFirst)
    try {
      return request(url, cacheControlFirst)
    } catch (err: Exception) {
      log.error(err.message, err)
    }

    return request(url, cacheControlSecond)
  }

  @Throws(IOException::class)
  fun request(
    url: String
  ) = client.newCall(Request.Builder().url(url).build()).execute()

  fun request(
    url: String,
    maxStale: Int //seconds
  ) = request(url, CacheControl.Builder().maxStale(maxStale, TimeUnit.SECONDS).build())

  @Throws(IOException::class)
  fun request(
    url: String,
    cacheControl: CacheControl
  ): Response {

    val request = Request.Builder()
      .url(url)
      .cacheControl(cacheControl)
      .build()


    return client.newCall(request).execute()
  }



  companion object {
    @Volatile
    private var INSTANCE: HttpSupport? = null

    fun getInstance(context: Context) = INSTANCE ?: synchronized(
      HttpSupport::class) {
      INSTANCE
        ?: HttpSupport(context).also {
        INSTANCE = it
      }
    }
  }
}

val Context.httpSupport: HttpSupport
  get() = HttpSupport.getInstance(this)

private val log = org.slf4j.LoggerFactory.getLogger(HttpSupport::class.java)

