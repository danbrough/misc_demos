package danbroid.demo.media2.content


import android.graphics.Color
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.media2.common.MediaMetadata
import androidx.media2.common.UriMediaItem
import androidx.media2.player.MediaPlayer
import danbroid.demo.media2.R
import danbroid.demo.media2.media.client.AudioClient
import danbroid.demo.media2.media.client.AudioTest
import danbroid.demo.media2.model.AudioClientModel
import danbroid.util.menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.util.*

const val URI_CONTENT_ROOT = "demo://content"

private val log = LoggerFactory.getLogger("danbroid.demo.media2.content")

private val MenuActionContext.audioClient: AudioClient
  get() = fragment!!.viewModels<AudioClientModel>().value.client

val rootContent: MenuItemBuilder =
  rootMenu<MenuItemBuilder> {
    id = URI_CONTENT_ROOT
    titleID = R.string.app_name

    log.error("RUNNING THIS BIT")


    menu {
      title = "Player Test"
      onClick = {
        AudioTest(context).run()
      }
    }

    menu {
      title = "Play"
      onClick = {
        audioClient.play()
      }
    }
    menu {
      title = "Prepare"
      onClick = {
        audioClient.prepare()
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
  }


