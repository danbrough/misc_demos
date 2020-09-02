package danbroid.media.service

import android.app.Service
import android.content.Intent
import android.os.*
import danbroid.media.messages.AppMessage
import danbroid.media.messages.RegisterEvent
import danbroid.media.messages.asAppMessage
import danbroid.media.util.debugToast
import java.lang.ref.WeakReference

class AudioService : Service() {

  private class MessageHandler(service: AudioService) : Handler() {

    val serviceRef = WeakReference(service)

    override fun handleMessage(msg: Message) =
      serviceRef.get()?.handleMessage(msg) ?: Unit
  }


  val messenger = Messenger(MessageHandler(this))

  val clients = mutableSetOf<Messenger>()

  val playerManager: PlayerManager by lazy {
    PlayerManager(this)
  }

  fun broadcastMessage(msg: AppMessage) {
    clients.forEach {
      try {
        it.send(msg.toMessage())
      } catch (err: RemoteException) {
        log.error(err.message, err)
        clients.remove(it)
      }
    }
  }

  fun handleMessage(m: Message) {

    try {
      val msg = m.asAppMessage()

      when (msg) {
        RegisterEvent.REGISTER ->
          clients.add(m.replyTo)

        RegisterEvent.UNREGISTER ->
          clients.remove(m.replyTo)

        else -> playerManager.processMessage(msg)
      }

    } catch (err: Exception) {
      log.error(err.message, err)
    }
  }


  override fun onBind(intent: Intent?): IBinder? {
    log.debug("onBind()")
    return messenger.binder
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    log.warn("onStartCommand() $intent")
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onCreate() {
    log.info("onCreate() ${this.hashCode()}")
    super.onCreate()
    debugToast("AudioService created: ${hashCode()}")
  }

  override fun onDestroy() = playerManager.close()


}

private val log = org.slf4j.LoggerFactory.getLogger(AudioService::class.java)

