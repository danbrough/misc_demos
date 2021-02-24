package danbroid.util.demo.content

import androidx.core.os.bundleOf
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import danbroid.util.demo.DemoNavGraph
import danbroid.util.demo.maptiler.MapTilerDemo
import danbroid.util.menu.Icons
import danbroid.util.menu.MenuDSL
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.menu

@MenuDSL
fun MenuItemBuilder.mapTilerDemos() = menu {
  title = "MapTiler Demos"
  icon = Icons.iconicsIcon(FontAwesome.Icon.faw_map_pin)

  menu {
    title = "Demo1"
    icon = Icons.iconicsIcon(FontAwesome.Icon.faw_map)
    onClick = {
      findNavController().navigate(DemoNavGraph.dest.map_tiler_demo, bundleOf(DemoNavGraph.arg.map_tiler_demo to MapTilerDemo.DEMO1))
    }

  }

}