package danbroid.util.demo

import android.content.Intent
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import danbroid.util.menu.MenuConfiguration
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.navigateToMenuID

abstract class MenuActivity(@LayoutRes layoutID: Int) : AppCompatActivity(layoutID) {

  constructor() : this(R.layout.activity_main)


  init {
    MenuConfiguration.rootMenu = ::getRootMenu
  }

  protected abstract fun getRootMenu(): MenuItemBuilder

  protected abstract fun createNavGraph(navController: NavController): NavGraph?

  protected open val navHostFragment: NavHostFragment
    get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setSupportActionBar(findViewById(R.id.toolbar))

    val navController = navHostFragment.navController

    createNavGraph(navController)?.also {
      navController.graph = it
    }

    setupActionBarWithNavController(navController)
  }


  override fun setTitle(title: CharSequence?) {
    super.setTitle(title)
    setToolbarTitle(title)
  }

  protected open fun setToolbarTitle(title: CharSequence?) {
    findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title = title
  }

  override fun onSupportNavigateUp() = navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    log.info("onNewIntent() $intent")
    intent.data ?: return
    val navController = navHostFragment.navController
    if (!navController.handleDeepLink(intent)) {
      val menuID = intent.dataString!!
      log.debug("no deeplink for ${menuID} .. will try to find the menuID")
      navController.navigateToMenuID(menuID)
    }
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MenuActivity::class.java)
