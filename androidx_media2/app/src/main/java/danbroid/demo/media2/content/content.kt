package danbroid.demo.media2.content


import android.content.Context
import androidx.fragment.app.activityViewModels
import danbroid.demo.media2.R
import danbroid.demo.media2.model.AudioClientModel
import danbroid.media.service.AudioClient
import danbroid.media.service.testTracks
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.MenuItemClickContext
import danbroid.util.menu.menu
import danbroid.util.menu.rootMenu

//URI base for navigation deeplinks
const val URI_CONTENT_ROOT = "demo://content"


private val MenuItemClickContext.audioClient: AudioClient
  get() = fragment.activityViewModels<AudioClientModel>().value.client


fun rootContent(context: Context) = context.rootMenu<MenuItemBuilder> {
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



