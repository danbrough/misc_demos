package danbroid.exoservice.content.somafm

import android.content.Context
import danbroid.exoservice.content.MediaItemBuilder
import danbroid.exoservice.content.MediaItemDSL
import danbroid.exoservice.content.URI_CONTENT_PREFIX

const val CHANNELS_URL = "https://somafm.com/channels.json"
const val URI_CONTENT_SOMAFM = "$URI_CONTENT_PREFIX/somafm"

@MediaItemDSL
fun MediaItemBuilder.somafm(block: SomaFMBuilder.() -> Unit) =
  menuItem(SomaFMBuilder(context), block)


class SomaFMBuilder(context: Context) : MediaItemBuilder(context) {
  init {
    mediaID = URI_CONTENT_SOMAFM
    title = "SomaFM"
    imageURI = "https://h1.danbrough.org/nzrp/somafm.png"
    isBrowsable = true
    liveChildren = SomaFM(context).loadChannels()
  }

}






