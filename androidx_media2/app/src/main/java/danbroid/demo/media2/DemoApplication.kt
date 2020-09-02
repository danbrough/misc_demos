package danbroid.demo.media2

import android.app.Application

class DemoApplication : Application() {

  override fun onCreate() {
    log.info("onCreate()")
    super.onCreate()
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(DemoApplication::class.java)
