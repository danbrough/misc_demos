package danbroid.demo.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.core.view.WindowCompat
import danbroid.demo.compose.ui.DemoApp

class ComposeActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)
    setContent {
      DemoApp(onBackPressedDispatcher)
    }
  }
}


private val log = org.slf4j.LoggerFactory.getLogger(ComposeActivity::class.java)
