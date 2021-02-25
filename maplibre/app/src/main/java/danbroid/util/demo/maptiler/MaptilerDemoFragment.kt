package danbroid.util.demo.maptiler

import com.mapbox.mapboxsdk.maps.Style
import danbroid.util.demo.DemoNavGraph
import danbroid.util.demo.R
import danbroid.util.demo.mapbox.MapboxViewFragment
import danbroid.util.demo.utils.MapConstants
import danbroid.util.demo.utils.setLocation


enum class MapTilerDemo {
  DEMO1
}

class MaptilerDemoFragment : MapboxViewFragment() {
  val demoType: MapTilerDemo by lazy {
    requireArguments().get(DemoNavGraph.arg.map_tiler_demo) as MapTilerDemo
  }

  override fun onMapReady() {
    log.debug("onMapReady() $demoType")
    super.onMapReady()
    val styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=${getString(R.string.maptilerApiKey)}"

    mapboxMap.setLocation(MapConstants.LATLNG_HOME)

    Style.Builder().fromUri(styleUrl).also {
      mapboxMap.setStyle(it)
    }
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MaptilerDemoFragment::class.java)
