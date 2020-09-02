package danbroid.demo.domain

import org.junit.Test

import danbroid.demo.domain.MenuItem

class DomainTest {
  @Test
  fun test() {
    log.info("test()")
    /*  val menuItem = MenuItem("/", "Root Item")
      log.warn("created@! $menuItem")*/
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(DomainTest::class.java)

