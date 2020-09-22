package danbroid.demo.media2

import android.app.Application
import danbroid.media.service.Config
import danbroid.util.resource.toResourceColour

class DemoApplication : Application() {

  override fun onCreate() {
    log.info("onCreate()")
    Config.Notifications.apply {
      notificationColour = R.color.colorPrimary.toResourceColour(applicationContext)
      notificationIconTint = R.color.colorPrimaryLight.toResourceColour(applicationContext)
    }
    super.onCreate()
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(DemoApplication::class.java)
