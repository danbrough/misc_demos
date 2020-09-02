package danbroid.exoservice.content.somafm

import com.google.gson.annotations.SerializedName
import danbroid.media.domain.MediaItem

/*
{
		"id": "7soul",
		"title": "Seven Inch Soul",
		"description": "Vintage soul tracks from the original 45 RPM vinyl.",
		"dj": "Dion Watts Garcia",
		"genre": "oldies",
		"image": "https://api.somafm.com/img/7soul120.png",
		"largeimage": "https://api.somafm.com/logos/256/7soul256.png",
		"xlimage": "https://api.somafm.com/logos/512/7soul512.png",
		"twitter": "SevenInchSoul",
		"updated": "1396144686",
		"playlists": [
			{ "uri": "https://api.somafm.com/7soul130.pls", "format": "aac",  "quality": "highest" },
			{ "uri": "https://api.somafm.com/7soul.pls", "format": "mp3",  "quality": "high" },
			{ "uri": "https://api.somafm.com/7soul64.pls", "format": "aacp",  "quality": "high" },
			{ "uri": "https://api.somafm.com/7soul32.pls", "format": "aacp",  "quality": "low" }
		],
		"listeners": "68",
		"lastPlaying": "Major Lance - Must Be Love Coming Down"
	}
 */
data class SomaChannel(
  val id: String,
  val title: String,
  val description: String,
  val dj: String,
  val genre: String,
  val image: String,
  @SerializedName("largeimage")
  val largeImage: String,
  @SerializedName("xlimage")
  val extraLargeImage: String,
  val twitter: String,
  val updated: String,
  val listeners: String,
  val lastPlaying: String,
  val playlists: List<SomaPlaylist>
) {

  data class SomaPlaylist(val url: String, val format: String, val quality: String)
}


fun SomaChannel.asMediaItem(): MediaItem {
  // log.trace("converting channel to mediaitem : id: $id  title: $title")
  return MediaItem(
    "$URI_CONTENT_SOMAFM/$id",
    title,
    description,
    imageURI = this.image
  ).also {

    it.isPlayable = true

    /*it.subTitle = this.description
    // description = description,
    it.playable = true
    it.website = "https://somafm.com/$id"*/
    it.uri = selectPlaylist(playlists)

    //it.image = image
  }
}


//quality = "highest","high","low
fun selectPlaylist(playlists: List<SomaChannel.SomaPlaylist>, quality: String = "high"): String {
  return playlists.find { it.quality == quality && it.format == "aacp" }?.url
    ?: playlists.find { it.quality == quality }?.url ?: playlists[0].url
}












