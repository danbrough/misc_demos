package danbroid.util.demo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
  @Inject
  lateinit var message: Thang

  @Inject
  @Model
  @JvmField
  var model: String? = null
}

private val log = org.slf4j.LoggerFactory.getLogger(App::class.java)
