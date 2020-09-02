package danbroid.exoservice.content.rnz

import android.content.Context
import android.text.format.DateFormat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import danbroid.exoservice.BuildConfig
import danbroid.media.domain.MediaItem
import danbroid.media.util.httpSupport
import danbroid.util.resource.toDisplayText
import okhttp3.CacheControl
import okhttp3.Request
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class RNZUtils(val context: Context) {
  val httpSupport = context.httpSupport


  @Throws(IOException::class)
  fun loadRNZNews(): MediaItem {
    val request = Request.Builder().url(URL_RNZ_NEWS).build()

    val response = httpSupport.client.newCall(request).execute()

    if (!response.isSuccessful) {
      response.close()
      throw IOException(response.message)
    }

    response.body?.string()?.lines()?.forEach { line ->
      if (line.contains("radio-new-zealand-news")) {
        line.substringAfter("href=\"").split('/').forEach { part ->
          try {
            return loadProgramme(
              part.toLong()
            ).apply {
              //TODO fix up date formatting
              this.imageURI = NEWS_IMAGE
              this.title = "RNZ News ${DateFormat.getDateFormat(context).format(Date())}"
              subtitle = subtitle.toDisplayText(context) ?: ""
            }
          } catch (err: NumberFormatException) {
            //try again with next line
          }
        }
      }
    }

    throw IOException("Failed to parse $URL_RNZ_NEWS")
  }

  @Throws(IOException::class)
  fun loadProgramme(
    progID: Long
  ): MediaItem {
    log.trace("loadProgramme() $progID")

    val progURL = RNZProgramme.getProgrammeURL(progID)

    val response = httpSupport.cacheRequest(
      progURL,
      CacheControl.Builder().maxStale(Int.MAX_VALUE, TimeUnit.SECONDS).build()
    )

    response.use {
      val data = response.body!!.string()

      if (BuildConfig.DEBUG)
        log.trace(GsonBuilder().setPrettyPrinting().create().toJson(data))

      val job = JsonParser.parseString(data).asJsonObject["item"]
      val programme = Gson().fromJson(job, RNZProgramme::class.java)
      return programme.toMediaItem().apply {
        mediaID = "$URI_RNZ_PROGRAMME_PREFIX/$progID"
      }
    }

  }
}


val log = org.slf4j.LoggerFactory.getLogger(RNZUtils::class.java.`package`!!.name)
