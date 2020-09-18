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

//URI base for navigation deeplinks
const val URI_CONTENT_ROOT = "demo://content"


private val MenuActionContext.audioClient: AudioClient
  get() = fragment!!.activityViewModels<AudioClientModel>().value.client


val rootContent: MenuItemBuilder =
  rootMenu<MenuItemBuilder> {
    id = URI_CONTENT_ROOT
    titleID = R.string.app_name

    testTracks.testData.forEach { track ->
      menu {
        title = track.title
        subtitle = track.subtitle
        imageID = R.drawable.ic_music_note
        imageURI = track.imageURI

        onClick = {
          audioClient.playUri(track.id)
        }
      }
    }
  }



