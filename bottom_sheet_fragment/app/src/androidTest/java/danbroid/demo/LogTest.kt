package danbroid.demo

import org.junit.Test

class LogTest {
  @Test
  fun test1() {
    log.trace("test1() man")
    log.debug("debug")
    log.info("info")
    log.warn("warn")
    log.error("error")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(LogTest::class.java)
