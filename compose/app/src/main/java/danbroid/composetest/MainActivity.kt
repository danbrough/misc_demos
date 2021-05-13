package danbroid.composetest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import danbroid.composetest.ui.theme.ComposeTestTheme
import org.slf4j.LoggerFactory
import androidx.activity.compose.setContent

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

