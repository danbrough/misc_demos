package danbroid.composetest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import danbroid.composetest.ui.theme.ComposeTestTheme
import danbroid.composetest.ui.theme.TestText

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ComposeTestTheme {
        TestText()
      }
    }
  }
}

