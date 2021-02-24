package danbroid.util.demo.content

import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import danbroid.util.demo.DemoNavGraph
import danbroid.util.menu.Icons
import danbroid.util.menu.MenuDSL
import danbroid.util.menu.MenuItemBuilder
import danbroid.util.menu.menu

@MenuDSL
fun MenuItemBuilder.vtmDemos() = menu {
  title = "Vtm Demos"
  menu {
    title = "Simple Map"
    onClick = {
      findNavController().navigate(DemoNavGraph.dest.vtm_map_simple)
    }
    icon = Icons.iconicsIcon(GoogleMaterial.Icon.gmd_map)
  }
  menu {
    title = "Mapilion MVT"
    onClick = {
      findNavController().navigate(DemoNavGraph.dest.vtm_mapillion_mvt)
    }
    icon = Icons.iconicsIcon(GoogleMaterial.Icon.gmd_map)
  }

  menu {
    title = "Vtm Fragment Test"
    onClick = {
      findNavController().navigate(DemoNavGraph.dest.vtm_fragment)
    }
    icon = Icons.iconicsIcon(FontAwesome.Icon.faw_canadian_maple_leaf)

  }
}