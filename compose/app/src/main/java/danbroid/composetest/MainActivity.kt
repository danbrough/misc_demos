package danbroid.composetest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import danbroid.composetest.ui.theme.ComposeTestTheme
import danbroid.composetest.ui.theme.TestContent1

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ComposeTestTheme {
        TestContent1()
      }
    }
  }
}

