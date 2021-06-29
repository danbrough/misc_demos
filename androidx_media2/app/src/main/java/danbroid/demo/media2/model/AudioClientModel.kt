package danbroid.demo.media2.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import danbroid.media.service.AudioClient

class AudioClientModel(app: Application) : AndroidViewModel(app) {

  val client = AudioClient(app)

  init {
    log.debug("created AudioClientModel")
  }
}

private val log = danbroid.logging.getLog(AudioClientModel::class)