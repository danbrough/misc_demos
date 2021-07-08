package danbroid.media.service

import android.content.Context
import danbroid.util.misc.SingletonHolder


class AudioServiceConfig(context: Context) {
  var HTTP_CACHE_SIZE = 10 * 1024 * 1024L

  var library: GenericMediaLibrary = GenericMediaLibrary()

  companion object : SingletonHolder<AudioServiceConfig, Context>(::AudioServiceConfig)
}


val Context.audioServiceConfig: AudioServiceConfig
  get() = AudioServiceConfig.getInstance(this)

private val log = danbroid.logging.getLog(AudioServiceConfig::class)
