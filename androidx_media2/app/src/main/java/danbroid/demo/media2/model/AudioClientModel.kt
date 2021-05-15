package danbroid.demo.media2.model

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import danbroid.media.service.AudioClient

class AudioClientModel(app: Application) : AndroidViewModel(app) {
  val context: Context = app

  val client = AudioClient(context)

  init {
    log.debug("created AudioClientModel")
  }
}

private val log = danbroid.logging.getLog(AudioClientModel::class)