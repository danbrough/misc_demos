package danbroid.exoservice.viewmodels

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import danbroid.media.messages.AppMessage
import danbroid.media.messages.RegisterEvent
import danbroid.media.messages.asAppMessage
import danbroid.media.service.AudioService
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow


class AudioServiceConnection(val context: Context) {

  enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED;
  }

  private val connectJobs = mutableListOf<() -> Unit>()

  private val messageChannel = BroadcastChannel<AppMessage>(Channel.BUFFERED)

  val messages: Flow<AppMessage> = messageChannel.asFlow()

  private val _connectionState: MutableLiveData<ConnectionState> =
    object : MutableLiveData<ConnectionState>(ConnectionState.DISCONNECTED) {
      override fun setValue(value: ConnectionState) {
        super.setValue(value)
        if (value == ConnectionState.CONNECTED) {
          connectJobs.forEach {
            it.invoke()
          }
          connectJobs.clear()
        } else if (value == ConnectionState.DISCONNECTED) {
          connectJobs.clear()
        }
      }
    }

  val connectionState: LiveData<ConnectionState> = _connectionState


  private val inMessenger = Messenger(Handler {
    messageChannel.offer(it.asAppMessage())
  })

  private var outMessenger: Messenger? = null


  val serviceConnection = object : ServiceConnection {

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      log.info("onServiceConnected()")
      outMessenger = Messenger(service)
      sendMessage(RegisterEvent.REGISTER)
      _connectionState.value = ConnectionState.CONNECTED
    }

    override fun onServiceDisconnected(name: ComponentName) {
      log.warn("onServiceDisconnected()")
      outMessenger = null
      _connectionState.value = ConnectionState.DISCONNECTED
    }
  }


  fun connect() {
    log.info("connect() connectionState: ${_connectionState.value}")
    if (_connectionState.value == ConnectionState.DISCONNECTED) {
      log.warn("binding to service ..")
      context.bindService(
        Intent(context, AudioService::class.java),
        serviceConnection,
        Context.BIND_AUTO_CREATE
      )
      _connectionState.value = ConnectionState.CONNECTING
    }
  }

  fun disconnect() {
    log.info("disconnect() connectionState: ${_connectionState.value}")
    if (_connectionState.value != ConnectionState.DISCONNECTED) {
      sendMessage(RegisterEvent.UNREGISTER)
      log.trace("unbinding service ..")
      context.unbindService(serviceConnection)
      _connectionState.value = ConnectionState.DISCONNECTED
    }
  }


  fun whenConnected(job: () -> Unit) {
    if (connectionState.value == ConnectionState.CONNECTED)
      job.invoke()
    else connectJobs.add(job)
  }

  fun sendMessage(msg: AppMessage) = sendMessage(msg.toMessage())

  fun sendMessage(msg: Message) = whenConnected {
    try {
      outMessenger?.send(msg.also {
        it.replyTo = inMessenger
      })
    } catch (err: RemoteException) {
      log.error(err.message, err)
    }
  }


}

private val log = org.slf4j.LoggerFactory.getLogger(AudioServiceConnection::class.java)

