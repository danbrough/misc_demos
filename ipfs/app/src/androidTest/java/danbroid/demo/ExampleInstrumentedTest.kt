package danbroid.demo


import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleInstrumentedTest {
  @Test
  fun useAppContext() {
    // Context of the app under test.
    log.info("useAppContext()")
    val appContext = getApplicationContext<Context>()
    assertEquals("danbroid.demo", appContext.packageName)
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(ExampleInstrumentedTest::class.java)
