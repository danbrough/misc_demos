package danbroid.demo.media2.content


import androidx.fragment.app.viewModels
import danbroid.demo.media2.R
import danbroid.demo.media2.media.client.AudioClient
import danbroid.demo.media2.model.AudioClientModel
import danbroid.util.menu.MenuActionContext
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.menu
import danbroid.util.menu.rootMenu
import org.slf4j.LoggerFactory

const val URI_CONTENT_ROOT = "demo://content"

private val log = LoggerFactory.getLogger("danbroid.demo.media2.content")

private val MenuActionContext.audioClient: AudioClient
  get() = fragment!!.viewModels<AudioClientModel>().value.client


val rootContent: MenuItemBuilder =
  rootMenu<MenuItemBuilder> {
    id = URI_CONTENT_ROOT
    titleID = R.string.app_name
    menu {
      title = "Play"
      onClick = {
        audioClient.play()
      }
    }

    menu {
      title = "Test"
      onClick = {
        audioClient.test()
      }
    }

    menu {
      title = "Pause"
      onClick = {
        audioClient.togglePause()
      }
    }


    menu {
      title = "State"
      onClick = {
        audioClient.state()
      }
    }
    val rnz_url = "http://radionz-ice.streamguys.com/National_aac128"
    val url_u80s = "http://ice4.somafm.com/u80s-256-mp3"
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
      title = "MP3"
      onClick = {
        audioClient.playUri("http://192.168.1.2/music/Calexico/2015%20Edge%20Of%20The%20Sun/05%20cumbia%20de%20donde.mp3")
      }
    }
  }



