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
}

private fun String.urlEncode() = URLEncoder.encode(this, "UTF-8")


private val log = LoggerFactory.getLogger(PubSub::class.java)