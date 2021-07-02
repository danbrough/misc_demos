package danbroid.demo.media2.content


import android.content.Context
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import danbroid.demo.media2.R
import danbroid.demo.media2.model.AudioClientModel
import danbroid.media.client.AudioClient
import danbroid.media.service.testTracks
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.MenuItemClickContext
import danbroid.util.menu.menu
import danbroid.util.menu.rootMenu

//URI base for navigation deeplinks
const val URI_CONTENT_ROOT = "demo://content"


private val MenuItemClickContext.audioClient: AudioClient
  get() = fragment.activityViewModels<AudioClientModel>() {
    object: ViewModelProvider.NewInstanceFactory(){
      override fun <T : ViewModel?> create(modelClass: Class<T>)= AudioClientModel(fragment.requireContext()) as T
    }
  }.value.client


fun rootContent(context: Context) = context.rootMenu<MenuItemBuilder> {
  id = URI_CONTENT_ROOT
  titleID = R.string.app_name

  onCreate = {
    log.info("onCreate()")

    testTracks.testData.forEach { track ->
      log.info("track $track")
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

}

private val log = danbroid.logging.getLog("danbroid.demo.media2.content")


