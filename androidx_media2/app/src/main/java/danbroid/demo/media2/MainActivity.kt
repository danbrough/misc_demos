package danbroid.demo.media2

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media2.common.MediaMetadata
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import danbroid.demo.media2.content.URI_CONTENT_ROOT
import danbroid.demo.media2.content.rootContent
import danbroid.demo.media2.model.AudioClientModel
import danbroid.media.client.AudioClient
import danbroid.media.service.AudioService
import danbroid.util.menu.MenuActivity
import danbroid.util.menu.createMenuNavGraph
import kotlinx.coroutines.flow.collect


class MainActivity : MenuActivity() {


  private val rootContent by lazy {
    rootContent(this)
  }

  val audioClientModel: AudioClientModel by viewModels()

  override fun getRootMenu() = rootContent

  override fun createNavGraph(navController: NavController) =
      navController.createMenuNavGraph(this, defaultMenuID = URI_CONTENT_ROOT)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    lifecycleScope.launchWhenResumed {
      audioClientModel.client.playState.collect {
        log.trace("playerState: $it")
        if (it == AudioClient.PlayerState.ERROR) {
          Snackbar.make(findViewById(R.id.bottom_controls_fragment), "Error playing ${audioClientModel.client.currentItem.value?.metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)}", Snackbar.LENGTH_SHORT).show()
        }
      }
    }
  }

/*
  override fun onCreate(savedInstanceState: Bundle?) {
    log.info("onCreate()")

    super.onCreate(savedInstanceState)

    setContentView(R.layout.activity_main)
    setSupportActionBar(findViewById(R.id.toolbar))
    navHostFragment.navController.apply {
      createMenuGraph()
      setupActionBarWithNavController(this)
    }

    log.warn("intent $intent")
    log.warn("data:${intent?.data}")
    intent?.extras?.also {
      it.keySet()?.forEach {
        log.debug("KEY: $it ${intent?.extras?.get(it)}")
      }
    }
  }
*/


/*  override fun onNewIntent(intent: Intent?) {
    log.warn("onNewIntent!() $intent")
    log.warn("data:${intent?.data}")
    log.warn("extras:${intent?.extras}")

    super.onNewIntent(intent)
  }*/

  override fun onSupportNavigateUp() = navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()


  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)

    menu.add("Clear Playlist").setOnMenuItemClickListener {
      audioClientModel.client.clearPlaylist()
      true
    }

    if (BuildConfig.DEBUG) {
      menu.add("Check Services").setOnMenuItemClickListener {
        log.derror("service running: ${isServiceRunning(this, AudioService::class.java.name)}")
        true
      }
    }
    return true
  }

  companion object {

    private fun isServiceRunning(context: Context, serviceName: String): Boolean {
      var serviceRunning = false
      val am = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
      val l = am.getRunningServices(50)
      val i: Iterator<ActivityManager.RunningServiceInfo> = l.iterator()
      while (i.hasNext()) {
        val runningServiceInfo = i
            .next()
        if (runningServiceInfo.service.className == serviceName) {
          serviceRunning = true
          log.derror("service: $runningServiceInfo")
          if (runningServiceInfo.foreground) {
            //service run in foreground
            log.info("$serviceName is in foreground")
          }
        }
      }
      return serviceRunning
    }
  }


  override fun onPause() {
    super.onPause()
  }

  override fun onStop() {
    super.onStop()
    log.dinfo("onStop()")
    log.derror("service running: ${isServiceRunning(this, AudioService::class.java.name)}")

  }


  override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
      R.id.action_settings -> true
      else -> super.onOptionsItemSelected(item)
    }
  }

}

private val log = danbroid.logging.getLog(MainActivity::class)


