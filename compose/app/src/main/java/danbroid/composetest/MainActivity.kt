package danbroid.composetest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import danbroid.composetest.ui.theme.ComposeTestTheme
import org.slf4j.LoggerFactory

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    log.debug("onCreate()")
    setContent {
      log.debug("setContent()")
      ComposeTestTheme {
        NewsStory()
      }
    }
  }
}

private val log = LoggerFactory.getLogger(MainActivity::class.java)

