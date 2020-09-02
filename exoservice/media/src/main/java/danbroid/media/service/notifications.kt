package danbroid.media.service


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.NotificationUtil
import danbroid.media.Config
import danbroid.media.domain.MediaItem


fun createNotificationManager(
  service: AudioService,
  channelID: String = Config.Notifications.channelID,
  notificationID: Int = Config.Notifications.notificationID,
  notificationListener: PlayerNotificationManager.NotificationListener
): PlayerNotificationManager {

  NotificationUtil.createNotificationChannel(
    service, channelID, notificationID, 0, //TODO create a description
    NotificationUtil.IMPORTANCE_LOW
  )

  return object : PlayerNotificationManager(
    service, channelID, notificationID, PlayerDescriptionAdapter(service), notificationListener
  ) {


    override fun createNotification(
      player: Player,
      builder: NotificationCompat.Builder?,
      ongoing: Boolean,
      largeIcon: Bitmap?
    ): NotificationCompat.Builder? {
      return super.createNotification(player, builder, ongoing, largeIcon)?.also {
        it.setUsesChronometer(player.isCurrentWindowSeekable)
        it.setShowWhen(player.isCurrentWindowSeekable)
      }
    }

    override fun getActions(player: Player) = super.getActions(player).also {
      if (!player.hasPrevious()) it.remove(ACTION_PREVIOUS)
    }

  }.apply {
    setSmallIcon(Config.Notifications.statusBarIcon)

    if (Config.Notifications.notificationColour != 0)
      setColor(Config.Notifications.notificationColour)

    setUseNavigationActionsInCompactView(true)
    setFastForwardIncrementMs(0L)
    setRewindIncrementMs(0L)
    setUseChronometer(true)
    setColorized(true)
    // setUseStopAction(true)
  }
}


private val log = org.slf4j.LoggerFactory.getLogger(NotificationManager::class.java)

private class PlayerDescriptionAdapter(val service: AudioService) :
  PlayerNotificationManager.MediaDescriptionAdapter {

  val context: Context = service

  val iconUtils = IconUtils(context)

  val mediaItem: MediaItem?
    get() = service.playerManager.player.mediaItem

  override fun createCurrentContentIntent(player: Player) =
    service.packageManager?.getLaunchIntentForPackage(service.packageName)?.let { sessionIntent ->
      PendingIntent.getActivity(service, 0, sessionIntent, 0)
    }

  override fun getCurrentContentTitle(player: Player) = mediaItem?.title ?: ""

  override fun getCurrentContentText(player: Player) = mediaItem?.subtitle ?: ""

  val defaultIcon: Bitmap by lazy {
    iconUtils.drawableToBitmapIcon(Config.Notifications.defaultNotificationIcon)
  }

  override fun getCurrentLargeIcon(
    player: Player,
    callback: PlayerNotificationManager.BitmapCallback
  ) = iconUtils.loadIcon(mediaItem!!, defaultIcon, callback::onBitmap)


}


