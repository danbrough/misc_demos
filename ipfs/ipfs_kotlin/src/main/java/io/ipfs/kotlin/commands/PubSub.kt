package io.ipfs.kotlin.commands

import io.ipfs.kotlin.IPFSConnection
import okhttp3.HttpUrl
import org.slf4j.LoggerFactory
import java.net.URLEncoder

class PubSub(val connection: IPFSConnection) {
  fun pub(topic: String, data: String): String {
    val cmd = "pubsub/pub/${topic.urlEncode()}?arg=${data.urlEncode()}"
    log.debug("cmd: $cmd")
    return connection.callCmd(cmd)
      .use { it.string() }
  }

  fun sub(topic: String, callback: (String) -> Unit) {
    val cmd = "pubsub/sub/${topic.urlEncode()}"
    log.debug("cmd: $cmd")
    runCatching {
      connection.callCmd(cmd)
        .use {
          it.charStream().also {
            it.forEachLine {
              callback.invoke(it)
            }
          }
        }
    }.exceptionOrNull()?.also {
      log.error(it.message, it)
    }
  }
}

private fun String.urlEncode() = URLEncoder.encode(this, "UTF-8")


private val log = LoggerFactory.getLogger(PubSub::class.java)