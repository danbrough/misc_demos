package danbroid.util.demo

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.activity
import androidx.navigation.fragment.fragment
import danbroid.util.demo.mapbox.MapboxSupportFragment
import danbroid.util.demo.mapbox.OsmMapFragment
import danbroid.util.demo.maptiler.MapTilerDemo
import danbroid.util.demo.maptiler.MaptilerDemoFragment
import danbroid.util.demo.ui.HomeFragment
import danbroid.util.demo.ui.SettingsFragment
import danbroid.util.demo.vtm.MapilionMvtActivity
import danbroid.util.demo.vtm.SimpleMapActivity
import danbroid.util.demo.vtm.VtmMapFragment
import danbroid.util.menu.MenuNavGraph
import danbroid.util.menu.createMenuNavGraph
import danbroid.util.misc.UniqueIDS

const val URI_PREFIX = "${BuildConfig.URI_SCHEME}:/"
const val URI_CONTENT_PREFIX = "$URI_PREFIX/content"

object DemoNavGraph : UniqueIDS {

  val id = nextID()

  object dest {
    val vtm_mapillion_mvt = nextID()
    val vtm_map_simple = nextID()
    val home = nextID()
    val settings = nextID()
    val test = nextID()
    val map = nextID()
    val osm_map = nextID()
    val map_tiler_demo = nextID()
    val vtm_fragment = nextID()
  }

  object action {
    val settings = nextID()
  }

  object arg {
    val map_tiler_demo = "mapTilerDemo"
  }

  object deep_link {
    val settings = "$URI_PREFIX/settings"
    val test = "$URI_PREFIX/test"
    val map = "$URI_PREFIX/map"
    val home = URI_CONTENT_PREFIX
  }

}


fun NavController.createDemoNavGraph(context: Context, builder: NavGraphBuilder.() -> Unit = {}) =
    createMenuNavGraph(context, homeID = MenuNavGraph.dest.home, defaultMenuID = DemoNavGraph.deep_link.home
    ) {

      fragment<HomeFragment>(DemoNavGraph.dest.home) {
        label = "Home"
        deepLink(DemoNavGraph.deep_link.home)
      }

      fragment<MapboxSupportFragment>(DemoNavGraph.dest.map) {
        label = "Map"
        deepLink(DemoNavGraph.deep_link.map)
      }

      fragment<OsmMapFragment>(DemoNavGraph.dest.osm_map) {
        label = "OSM Map"
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

      fragment<MaptilerDemoFragment>(DemoNavGraph.dest.map_tiler_demo) {
        label = "MapTiler Demo {mapTilerDemo}"
        argument(DemoNavGraph.arg.map_tiler_demo) {
          type = NavType.EnumType(MapTilerDemo::class.java)
          nullable = false
          defaultValue = MapTilerDemo.DEMO1
        }
      }

      fragment<VtmMapFragment>(DemoNavGraph.dest.vtm_fragment) {
        //label = "MapTiler Demo {mapTilerDemo}"
        /*argument(DemoNavGraph.arg.map_tiler_demo) {
          type = NavType.ParcelableType(MapTilerDemo::class.java)
          nullable = false
          defaultValue = MapTilerDemo.DEMO1
        }*/
      }

      activity(DemoNavGraph.dest.vtm_map_simple) {
        label = "Simple Map"
        activityClass = SimpleMapActivity::class
      }

      activity(DemoNavGraph.dest.vtm_mapillion_mvt) {
        label = "Mapilion MVT Map"
        activityClass = MapilionMvtActivity::class
      }

      builder()
    }



