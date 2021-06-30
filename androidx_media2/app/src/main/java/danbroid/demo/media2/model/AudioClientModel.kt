package danbroid.demo.media2.model

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import danbroid.media.service.AudioClient

class AudioClientModel(context: Context) : ViewModel() {

  val client = AudioClient(context)

  init {
    log.derror("created AudioClientModel")
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

private val log = danbroid.logging.getLog(AudioClientModel::class)