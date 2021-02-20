package danbroid.util.demo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var thang: Thang

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)
    log.info("onCreate() thang: $thang")
    findViewById<Button>(R.id.button1).setOnClickListener {
      thang.count++
      log.debug("thang is $thang")
      findViewById<TextView>(R.id.text1).text = "${thang}"
    }

    findViewById<TextView>(R.id.text1).text = "${thang}"
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MainActivity::class.java)
