package danbroid.exoservice.content.rnz

import com.google.gson.annotations.SerializedName
import danbroid.media.domain.MediaItem
import java.security.MessageDigest


/*
 {
  "item": {
    "id": 201829978,
    "audio_path": "/national/programmes/nat-music/audio/201829978/music-101-pocket-edition-119",
    "audio_title_length": 28,
    "title": "Music 101 Pocket Edition 119",
    "body": "\u003cp\u003eThis week, a candid chat with The Naked And Famous, British producer Sohn, and Wellington band Draghound introduce themselves, plus new tunes from The Shins, Sampha, and David Bowie.\u003c/p\u003e\r\n",
    "programme_name": "RNZ Music",
    "programme_path": "/national/programmes/nat-music",
    "station_name": "National",
    "broadcast_date": "17 Jan 2017",
    "broadcast_at": "2017-01-17 16:00:00",
    "host": "Alex Behan",
    "audio": {
      "ogg": {
        "uri": "http://on-demand.radionz.co.nz/natmusic/natmusic-20170117-1600-music_101_pocket_edition_119-03.ogg",
        "size": 40808775
      },
      "mp3": {
        "uri": "http://on-demand.radionz.co.nz/natmusic/natmusic-20170117-1600-music_101_pocket_edition_119-192.mp3",
        "size": 79267170
      }
    },
    "duration": "55′00″",
    "thumbnail": "/assets/news_crops/25210/eight_col_thenakedandfamous2016_1240.jpg?1484106948"
  }
}

 */
data class RNZProgramme(

  private val id: Long,

  @SerializedName("audio_path")
  val audioPath: String,

  @SerializedName("audio_title_length")
  val audioTitleLength: Int,

  val title: String,

  val body: String,

  @SerializedName("programme_name")
  val programmeName: String,

  @SerializedName("programme_path")
  val programmePath: String,

  @SerializedName("station_name")
  val stationName: String,

  @SerializedName("broadcast_date")
  val broadcastDate: String,

  @SerializedName("broadcast_at")
  val broadcastAt: String,

  val host: String,

  val duration: String,

  val thumbnail: String?,

  val audio: Audio
) {

  data class AudioLink(val url: String, val size: Long)

  data class Audio(val ogg: AudioLink?, val mp3: AudioLink?)

  fun toMediaItem() = MediaItem("$id", programmeName, body).apply {
    uri = audio.mp3?.url ?: audio.ogg!!.url
    isPlayable = true
    website = "https://www.rnz.co.nz$audioPath"
    imageURI = if (thumbnail != null) "http://www.rnz.co.nz$thumbnail" else null
  }

  companion object {
    fun bytesToHex(bytes: ByteArray) =
      bytes.map { b -> String.format("%02x", b) }.joinToString("", "", "")

    fun getProgrammeID(id: Long): String {
      var progID = "${id}q6kzN3TQ29ubhUUhdOcirS0fiNITkMMLR5HyU5Sv"
      val digest = MessageDigest.getInstance("SHA1").digest(progID.toByteArray())
      progID = bytesToHex(digest) + String.format("%08x", id)
      return progID
    }

    fun getProgrammeURL(id: Long) =
      "https://www.rnz.co.nz/audio/pdata/${getProgrammeID(id)}.json"

  }

}


