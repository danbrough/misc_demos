package danbroid.demo.media2.content


import androidx.fragment.app.activityViewModels
import danbroid.demo.media2.R
import danbroid.demo.media2.model.AudioClientModel
import danbroid.media.service.AudioClient
import danbroid.util.menu.MenuActionContext
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.menu
import danbroid.util.menu.rootMenu
import org.slf4j.LoggerFactory

const val URI_CONTENT_ROOT = "demo://content"

private val log = LoggerFactory.getLogger("danbroid.demo.media2.content")

private val MenuActionContext.audioClient: AudioClient
  get() = fragment!!.activityViewModels<AudioClientModel>().value.client

private const val rnz_url = "http://radionz-ice.streamguys.com/National_aac128"
private const val url_u80s = "http://ice4.somafm.com/u80s-256-mp3"

val rootContent: MenuItemBuilder =
  rootMenu<MenuItemBuilder> {
    id = URI_CONTENT_ROOT
    titleID = R.string.app_name



    menu {
      title = "Play"
      onClick = {
        audioClient.mediaController.play()
      }
    }



    menu {
      title = "U80s"
      onClick = {
        log.trace("playing U80s")
        audioClient.playUri(url_u80s)
      }
    }

    menu {
      title = "RNZ"
      onClick = {
        audioClient.playUri(rnz_url)
      }
    }
    menu {
      title = "Opus Test"
      onClick = {
        audioClient.playUri("https://h1.danbrough.org/guitar/improv/improv1.opus")
      }
    }

    menu {
      title = "Flac Test"
      onClick = {
        audioClient.playUri("https://h1.danbrough.org/guitar/improv/improv2.flac")
      }
    }

    menu {
      title = "MP3 Test"
      onClick = {
        audioClient.playUri("https://h1.danbrough.org/guitar/improv/improv3.mp3")
      }
    }

    menu {
      title = "Ogg Test"
      onClick = {
        audioClient.playUri("https://h1.danbrough.org/guitar/improv/improv4.ogg")
      }
    }
    val oggtest2 = "http://192.168.1.2/music/Electric%20Youth/Innerworld/02%20-%20Runaway.ogg"
    menu {
      title = "Ogg Tes2"
      onClick = {
        audioClient.playUri(oggtest2)
      }
    }

    menu {
      title = "Short OGA"
      onClick = {
        val uri = "https://h1.danbrough.org/media/tests/test.oga"
        audioClient.playUri(uri)
      }
    }


  }



