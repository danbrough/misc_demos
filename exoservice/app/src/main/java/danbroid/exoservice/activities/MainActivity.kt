package danbroid.exoservice.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import danbroid.exoservice.R
import danbroid.exoservice.player
import danbroid.exoservice.ui.medialist.MediaListAdapter
import danbroid.exoservice.ui.medialist.MediaListFragmentDirections
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), ActivityInterface {


  protected val navController: NavController
    get() = findNavController(R.id.nav_host_fragment)

  lateinit var appBarConfig: AppBarConfiguration


  override fun onCreate(savedInstanceState: Bundle?) {
    log.info("onCreate()")
    super.onCreate(savedInstanceState)


    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    appBarConfig = AppBarConfiguration.Builder(navController.graph).build()

    setupActionBarWithNavController(
      navController,
      appBarConfig
    )


    var lastQueueSize = 0

    player.playbackQueue.observe(this) {
      log.error("PLAYBACK QUEUE: ${it.queue.size} lastSize: $lastQueueSize")
      if (it.queue.size != lastQueueSize) {
        lastQueueSize = it.queue.size
        invalidateOptionsMenu()
      }
    }

/*
    if (savedInstanceState == null)
      supportFragmentManager.commit {
        replace(R.id.bottom_controls_fragment, BottomControlsFragment())
      }
*/




  }

  override fun onItemClicked(holder: MediaListAdapter.MediaItemViewHolder) {
    val item = holder.item!!

    if (item.isBrowsable) {
      MediaListFragmentDirections.actionGlobalNavigationMedialist(holder.item!!.mediaID).let {
        navController.navigate(it)
      }
    } else if (item.isPlayable) {
      lifecycleScope.launch {
        player.play(item)
      }
    }
  }

  override fun onSupportNavigateUp() = navController.navigateUp() || super.onSupportNavigateUp()

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_queue -> {
        navController.navigate(MediaListFragmentDirections.actionGlobalNavigatePlaylist())
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun showSnackbar(
    msg: CharSequence,
    length: Int,
    onUndo: (() -> Unit)?
  ) {
    Snackbar.make(coordinator, msg, length).apply {
      onUndo?.also {
        setAction(R.string.lbl_undo, {
          onUndo.invoke()
        })
      }
    }
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MainActivity::class.java)


