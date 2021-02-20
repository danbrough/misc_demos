package danbroid.util.demo

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.fragment.fragment
import danbroid.util.demo.ui.BrowserFragment
import danbroid.util.demo.ui.HomeFragment
import danbroid.util.demo.ui.MapFragment
import danbroid.util.demo.ui.SettingsFragment
import danbroid.util.menu.createMenuNavGraph
import danbroid.util.misc.UniqueIDS

const val URI_PREFIX = "${BuildConfig.URI_SCHEME}:/"
const val URI_CONTENT_PREFIX = "$URI_PREFIX/content"

object DemoNavGraph : UniqueIDS {
  val id = nextID()

  object dest {
    val home = nextID()
    val browser = nextID()
    val map = nextID()
    val settings = nextID()
    val test = nextID()
  }

  object action {
    val settings = nextID()
  }

  object deep_link {
    val settings = "$URI_PREFIX/settings"
    val test = "$URI_PREFIX/test"
    val home = URI_CONTENT_PREFIX
  }

}

fun NavController.createDemoNavGraph(context: Context,homeID:Int = DemoNavGraph.dest.map, builder: NavGraphBuilder.() -> Unit = {}) =
    createMenuNavGraph(context, homeID = homeID, defaultMenuID = DemoNavGraph.deep_link.home) {

      fragment<HomeFragment>(DemoNavGraph.dest.home) {
        label = "Home"
        deepLink(DemoNavGraph.deep_link.home)

      }
      fragment<BrowserFragment>(DemoNavGraph.dest.browser) {
        label = "Browser"
      }

      fragment<MapFragment>(DemoNavGraph.dest.map) {
        label = "Map"
      }
      fragment<HomeFragment>(DemoNavGraph.dest.test) {
        label = "Test"
        deepLink(DemoNavGraph.deep_link.test)
      }

      fragment<SettingsFragment>(DemoNavGraph.dest.settings) {
        label = context.getString(R.string.lbl_settings)
        deepLink(DemoNavGraph.deep_link.settings)
      }

      action(DemoNavGraph.action.settings) {
        destinationId = DemoNavGraph.dest.settings
      }

      builder()
    }



