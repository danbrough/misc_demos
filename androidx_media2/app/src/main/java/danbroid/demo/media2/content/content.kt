package danbroid.demo.media2.content


import androidx.fragment.app.activityViewModels
import danbroid.demo.media2.R
import danbroid.demo.media2.model.AudioClientModel
import danbroid.media.service.AudioClient
import danbroid.media.service.testTracks
import danbroid.util.menu.MenuActionContext
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.menu
import danbroid.util.menu.rootMenu
import org.slf4j.LoggerFactory

const val URI_CONTENT_ROOT = "demo://content"

private val log = LoggerFactory.getLogger("danbroid.demo.media2.content")

private val MenuActionContext.audioClient: AudioClient
  get() = fragment!!.activityViewModels<AudioClientModel>().value.client


val rootContent: MenuItemBuilder =
    rootMenu<MenuItemBuilder> {
      id = URI_CONTENT_ROOT
      titleID = R.string.app_name

      menu {
        title = "Play"
        onClick = {
          audioClient.callPlay()
        }
      }

      testTracks.testData.forEach { track ->
        menu {
          title = track.title
          subtitle = track.subtitle
          onClick = {
            audioClient.playUri(track.id)
          }
        }
      }
    }



