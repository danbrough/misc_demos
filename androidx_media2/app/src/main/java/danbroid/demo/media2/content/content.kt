package danbroid.demo.media2.content


import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
  get() = fragment!!.activityViewModels<AudioClientModel>().value.client

private const val rnz_url = "http://radionz-ice.streamguys.com/National_aac128"
private const val url_u80s = "http://ice4.somafm.com/u80s-256-mp3"
private const val flac_url =
  "http://192.168.1.2/music/Blue%20Nile%2CThe/1984%20A%20Walk%20Across%20The%20Rooftops/04%20-%20Stay.flac"
private const val ogg_url = "http://192.168.1.2/music/Air/Moon%20Safari/06_remember.mp3"
private const val mp3_url =
  "http://192.168.1.2/music/BB%20King/Why%20I%20Sing%20The%20Blues/B.B.%20King%20-%20Why%20I%20Sing%20The%20Blues.mp3"
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
      title = "Parent"
      menu {
        title = "Child"
        onClick = {
          fragment?.activity?.also {
            it.findViewById<View>(R.id.bottom_controls).also {
              log.warn("Got view: $it")
              BottomSheetBehavior.from(it).also {
                log.warn("behaviour: $it")
                it.state = BottomSheetBehavior.STATE_EXPANDED
              }
            }
          }
        }
      }
    }
/*

    menu {
      title = "Test u80s"
      onClick = {
        audioClient.test.play(url_u80s)
      }
    }

    menu {
      title = "Test Flac"
      onClick = {
        audioClient.test.play(flac_url)
      }
    }


    menu {
      title = "Test Ogg"
      onClick = {
        audioClient.test.play(ogg_url)
      }
    }
    menu {
      title = "Test Mp3"
      onClick = {
        audioClient.test.play(mp3_url)
      }
    }

    menu {
      title = "Test Pause"
      onClick = {
        audioClient.test.togglePause()
      }
    }
*/

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

    menu {
      title = "Opus Test"
      onClick = {
        audioClient.playUri("http://192.168.1.2/music/Aldus%20Harding/2019%20Designer/02%20-%20Designer.opus")
      }
    }

    menu {
      title = "Flac Test"
      onClick = {
        audioClient.playUri(flac_url)
      }
    }
    /*



  menu {
    title = "Short OGG"
    isPlayable = true
    uri = "https://h1.danbrough.org/media/tests/test.ogg"
    imageID = R.drawable.ic_kiwi

  }


  menu {
    title = "Short OGA"
    isPlayable = true
    uri = "https://h1.danbrough.org/media/tests/test.oga"
    imageID = R.drawable.ic_favorite
  }
     */
  }



