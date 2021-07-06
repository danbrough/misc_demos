package danbroid.demo.media2

import android.app.Application
import danbroid.demo.media2.content.SomaFM
import danbroid.logging.AndroidLog
import danbroid.logging.LogConfig
import danbroid.media.service.Config
import danbroid.media.service.TestDataLibrary
import danbroid.media.service.audioServiceConfig
import danbroid.util.resource.toResourceColour

class DemoApplication : Application() {


  val log = LogConfig.let {
    val log = AndroidLog("DEMO")
    it.GET_LOG = { log }
    it.COLOURED = true
    it.DEBUG = BuildConfig.DEBUG
    it.DETAILED = true
    log.debug("created log")
    log
  }


  override fun onCreate() {
    log.info("onCreate()")
    audioServiceConfig.library.register(TestDataLibrary(), SomaFM.getInstance(this))
    Config.Notifications.apply {
      notificationColour = R.color.colorPrimary.toResourceColour(applicationContext)
      notificationIconTint = R.color.colorPrimaryLight.toResourceColour(applicationContext)
    }
    super.onCreate()
  }
}

