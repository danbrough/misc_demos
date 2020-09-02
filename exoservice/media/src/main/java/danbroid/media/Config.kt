package danbroid.media

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

/**
 * Configuration for the danbroid.media package
 */
object Config {


  object Notifications {

    val channelID: String = "ExoService"

    var notificationID = 1438292

    @DrawableRes
    var statusBarIcon = R.drawable.ic_kiwi

    @ColorInt
    var notificationColour = 0

    @ColorInt
    var notificationIconTint = 0

    @DrawableRes
    var defaultNotificationIcon = R.drawable.ic_audiotrack
  }

}


