package danbroid.demo.domain
import danbroid.demo.domain.MenuItem


class MenuTest {
  fun run() {
    log.debug("running menu test..")
    val menu = MenuItem("id", "Menu Title")
    log.debug("created $menu")
  }
}

fun main() {
  MenuTest().run()
}

private val log = org.slf4j.LoggerFactory.getLogger(MenuTest::class.java)

