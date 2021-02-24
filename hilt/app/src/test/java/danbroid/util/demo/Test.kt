package danbroid.util.demo

import kotlinx.coroutines.*


class ClickContext {
  var consumed: Boolean = false
}


class Test {


  @org.junit.Test
  fun test() {
    log.info("OK")

    runBlocking {
      val context = ClickContext()
      launch {
        context.clickHandler()
        log.debug("here")
        delay(2000)

      }.invokeOnCompletion {
        log.debug("click handler done: ${context.consumed}")
      }

    }
  }


  suspend fun ClickContext.clickHandler() {
    log.info("clickHandler()")

    consumed = true

    coroutineScope {
      launch(Dispatchers.IO) {
        log.info("launch started..")
        delay(2000)
        log.info("launch finished")
        consumed = false
      }
    }
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(Test::class.java)
