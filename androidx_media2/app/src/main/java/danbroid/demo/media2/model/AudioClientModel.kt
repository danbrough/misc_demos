package danbroid.demo.media2.model

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import danbroid.media.client.AudioClient
import danbroid.media.service.AudioService

class AudioClientModel(context: Context) : ViewModel() {

  val client = AudioClient(context)

  init {
    log.derror("created AudioClientModel")
    context.startService(Intent(context, AudioService::class.java))
  }

  override fun onCleared() {
    super.onCleared()
    log.info("onCleared()")
    client.close()
  }
}


val Fragment.audioClientModel: AudioClientModel
  get() = activityViewModels<AudioClientModel> {
    object : ViewModelProvider.NewInstanceFactory() {
      override fun <T : ViewModel?> create(modelClass: Class<T>) = AudioClientModel(requireContext()) as T
    }
  }.value


val ComponentActivity.audioClientModel: AudioClientModel
  get() = viewModels<AudioClientModel> {
    object : ViewModelProvider.NewInstanceFactory() {
      override fun <T : ViewModel?> create(modelClass: Class<T>) = AudioClientModel(this@audioClientModel) as T
    }
  }.value

private val log = danbroid.logging.getLog(AudioClientModel::class)