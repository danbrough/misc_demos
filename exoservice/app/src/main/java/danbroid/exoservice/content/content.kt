package danbroid.exoservice.content

import android.content.Context
import danbroid.exoservice.BuildConfig
import danbroid.exoservice.R
import danbroid.exoservice.content.rnz.rnzNews
import danbroid.exoservice.content.somafm.somafm
import danbroid.media.domain.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.slf4j.LoggerFactory

const val URI_SCHEME = "exodemo"
const val URI_PREFIX = "$URI_SCHEME:/"
const val URI_CONTENT_PREFIX = "$URI_PREFIX/content"
const val URI_MEDIA_PREFIX = "$URI_PREFIX/media"


fun rootContent(context: Context) = rootMenu(context) {

  mediaID = URI_CONTENT_PREFIX
  titleID = R.string.app_name

  menu {
    title = "Opus Test"
    isPlayable = true
    uri = "http://192.168.1.2/music/Aldus%20Harding/2019%20Designer/02%20-%20Designer.opus"
  }

  if (BuildConfig.DEBUG) {
    menu {
      mediaID = "$URI_CONTENT_PREFIX/test"
      title = "Test"
      isBrowsable = true
      liveItem = flow {
        log.trace("testItem()")
        delay(1000)
        (0..10).forEach {
          title = "TEst $it"
          log.trace("emitting $it")
          emit(buildItem())
          delay(500)
        }
      }

      liveChildren = flow {
        log.trace("liveChildren FLOW")
        delay(1000)
        var n = 0
        while (true) {
          n++
          val items = mutableListOf<MediaItem>()
          (0..n).forEach {
            items.add(MediaItem("$mediaID/$it", "Child $it", ""))
          }
          delay(500)
          log.trace("emitting children $n")
          emit(items)
        }
      }.flowOn(Dispatchers.IO)
    }
  }

  rnzNews()

  menu {
    title = "Flac Test"
    uri =
      "http://192.168.1.2/music/Blue%20Nile%2CThe/1984%20A%20Walk%20Across%20The%20Rooftops/04%20-%20Stay.flac"
    isPlayable = true
  }

  menu {
    title = "Short OGA"
    isPlayable = true
    uri = "https://h1.danbrough.org/media/tests/test.oga"
    imageID = R.drawable.ic_favorite
  }

  menu {
    title = "Short OGG"
    isPlayable = true
    uri = "https://h1.danbrough.org/media/tests/test.ogg"
    imageID = R.drawable.ic_kiwi

  }

  menu {
    title = "RNZ"
    isPlayable = true
    uri = "http://radionz-ice.streamguys.com/National_aac128"
    imageID = R.drawable.ic_fern2
  }

  somafm {
  }

  menu {
    mediaID = "$URI_CONTENT_PREFIX/metalradio"
    //website = "http://www.metalradio.co.nz/"
    title = "Metal Radio"
    subtitle = "New Zealand's Premiere Metal Radio Station, on the (digital) air since 2011!"
    uri = "http://curiosity.shoutca.st:9073/stream"
    imageURI = "https://h1.danbrough.org/nzrp/metalradio.png"
    isPlayable = true
  }


  playbackQueue {
    isHidden = true
  }

}

val log = LoggerFactory.getLogger("danbroid.exoservice.content")