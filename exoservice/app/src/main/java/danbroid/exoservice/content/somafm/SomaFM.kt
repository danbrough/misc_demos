package danbroid.exoservice.content.somafm

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import danbroid.media.util.httpSupport
import danbroid.media.domain.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.CacheControl
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class SomaFM(val context: Context) {


  fun loadChannels() = flow {
    log.trace("loadChannels()")
    val httpClient = context.httpSupport.client
    var readChannels = false

    var errMessage = ""

    try {
      val call = httpClient.newCall(
        Request.Builder()
          .cacheControl(CacheControl.FORCE_CACHE)
          .url(CHANNELS_URL).build()
      )
      val response = call.execute()

      response.use {
        if (response.isSuccessful) {
          val channels = parseChannels(response)
          emit(channels)
          readChannels = true
        } else {
          throw IOException("failed to access somafm error:${response.message}")
        }
      }

    } catch (err: IOException) {
      log.error("Failed to parseChannels from cache: ${err.message}")
      errMessage = err.message ?: ""
    }

    try {
      val call = httpClient.newCall(
        Request.Builder()
          .url(CHANNELS_URL).build()
      )
      val response = call.execute()

      response.use {
        if (response.isSuccessful) {
          val channels = parseChannels(response)
          if (response.networkResponse!= null || !readChannels)
            emit(channels)
          readChannels = true
        } else {
          throw IOException("failed to access somafm error:${response.message}")
        }
      }

    } catch (err: IOException) {
      log.error(err.message, err)
      errMessage = err.message ?: ""
    }

    if (!readChannels) throw IOException("Failed to load Soma channels: $errMessage")

  }.flowOn(Dispatchers.IO)

  private fun parseChannels(response: Response): List<MediaItem> {
    val data = JsonParser.parseReader(response.body!!.charStream())
    val channels: List<SomaChannel> = Gson().fromJson(
      data.asJsonObject["channels"],
      object : TypeToken<ArrayList<SomaChannel>>() {}.type
    )
    return channels.map { it.asMediaItem() }
  }

}

val log = org.slf4j.LoggerFactory.getLogger(SomaFM::class.java)

