package danbroid.util.demo.content

import android.content.Context
import android.content.Intent
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import danbroid.util.demo.DemoNavGraph
import danbroid.util.demo.R
import danbroid.util.demo.SimpleMapActivity
import danbroid.util.demo.URI_CONTENT_PREFIX
import danbroid.util.menu.Icons
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.menu
import danbroid.util.menu.rootMenu
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.slf4j.LoggerFactory

private object _content

private val log = LoggerFactory.getLogger(_content::class.java.`package`!!.name)

@ExperimentalCoroutinesApi
fun rootContent(context: Context) = context.rootMenu<MenuItemBuilder> {
  id = URI_CONTENT_PREFIX
  titleID = R.string.app_name



  menu {
    title = "Simple Map Activity"
    onClick = {
      context.startActivity(Intent(context, SimpleMapActivity::class.java))
    }

    menu {
      title = "Fragment Test"
      id = DemoNavGraph.deep_link.test
    }
  }

  menu {
    title = "Map Fragment"
    id = DemoNavGraph.deep_link.map
    icon = Icons.iconicsIcon(GoogleMaterial.Icon.gmd_map)
  }

  menu {
    title = "OSM Map Fragment"
    onClick = {
      findNavController().navigate(DemoNavGraph.dest.osm_map)
    }
    icon = Icons.iconicsIcon(GoogleMaterial.Icon.gmd_map)
  }

  mapTilerDemos()

  vtmDemos()


  menu {
    titleID = R.string.lbl_settings
    id = DemoNavGraph.deep_link.settings
    icon = Icons.iconicsIcon(GoogleMaterial.Icon.gmd_settings)
  }


}
