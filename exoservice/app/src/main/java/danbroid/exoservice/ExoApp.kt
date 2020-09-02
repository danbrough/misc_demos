package danbroid.exoservice

import android.app.Application
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import danbroid.exoservice.viewmodels.AudioPlayer
import danbroid.media.Config


class ExoApp : Application() {


  val player: AudioPlayer by lazy {
    AudioPlayer(this)
  }

  override fun onCreate() {

    super.onCreate()


    Config.Notifications.notificationColour =
      ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme)
    Config.Notifications.notificationIconTint =
      ResourcesCompat.getColor(resources, R.color.colorPrimaryLight, theme)
  }


}

val Context.player
  get() = (this.applicationContext as ExoApp).player

private val log = org.slf4j.LoggerFactory.getLogger(ExoApp::class.java)

