package danbroid.demo

class Test1 {
  fun run() {
    log.info("running test")
  }
}

fun main() {
  Test1().run()
}

private val log = org.slf4j.LoggerFactory.getLogger(Test1::class.java)

