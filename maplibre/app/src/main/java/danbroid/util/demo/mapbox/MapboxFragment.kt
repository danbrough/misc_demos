package danbroid.util.demo.mapbox

import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.utils.BitmapUtils
import danbroid.util.demo.R
import danbroid.util.demo.utils.MapConstants
import danbroid.util.demo.utils.setLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapboxFragment : MapboxViewFragment() {
  companion object {
    const val ID_IMAGE_MARKER = "marker"
  }

  override fun onMapReady() {
    mapboxMap.setLocation(MapConstants.LATLNG_HOME)
    val style = Style.Builder().fromUri(Style.MAPBOX_STREETS)
        .withImage(ID_IMAGE_MARKER, BitmapUtils.getDrawableFromRes(requireContext(), R.drawable.ic_place, ResourcesCompat.getColor(resources, R.color.colorAccent, requireContext().theme))!!)

    mapboxMap.setStyle(style, ::configureStyle)
  }

  private fun configureStyle(style: Style) {
    enableLocation(style)

    val symbolManager = SymbolManager(mapView, mapboxMap, style).apply {
      iconAllowOverlap = true
      iconIgnorePlacement = true
    }

    val symbol = symbolManager.create(SymbolOptions()
        .withLatLng(MapConstants.LATLNG_HOME)
        .withIconImage(ID_IMAGE_MARKER)
        .withIconSize(2.0f))

    lifecycleScope.launch {
      delay(2000)
      log.debug("moving symbol")
      symbol.latLng = LatLng(MapConstants.LATLNG_HOME.latitude + 0.004, MapConstants.LATLNG_HOME.longitude)
      symbolManager.update(symbol)
    }

  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MapboxFragment::class.java)
