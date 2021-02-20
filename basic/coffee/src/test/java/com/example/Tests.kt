package com.example

import org.junit.Test

class Tests {

  @Test
  fun test1() {
    log.info("test1()")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(Tests::class.java)
