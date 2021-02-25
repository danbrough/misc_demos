package danbroid.util.demo.mapbox

import danbroid.util.demo.utils.MapConstants
import danbroid.util.demo.utils.setLocation

class OsmMapFragment : MapboxViewFragment() {
  override val requireApiKey = false
  override fun onMapReady() {
    mapboxMap.setStyle(MapConstants.StyleOSM)
    mapboxMap.setLocation(MapConstants.LATLNG_HOME)
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(OsmMapFragment::class.java)
