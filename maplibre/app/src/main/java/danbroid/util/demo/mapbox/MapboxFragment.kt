package danbroid.util.demo.mapbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import danbroid.util.demo.R
import danbroid.util.demo.utils.MapConstants
import danbroid.util.demo.utils.initMapBox
import danbroid.util.demo.utils.setLocation

open class MapboxFragment : Fragment(R.layout.fragment_map) {

  protected lateinit var mapView: MapView
  protected lateinit var mapBoxMap: MapboxMap
  protected open val requireApiKey = true

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    requireContext().initMapBox(requireApiKey)
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")



    mapView = view.findViewById(R.id.mapView)
    mapView.onCreate(savedInstanceState)
    mapView.getMapAsync {
      mapBoxMap = it
      onMapReady()
    }
  }

  open fun onMapReady() {

    mapBoxMap.setLocation(MapConstants.LATLNG_HOME)
    mapBoxMap.setStyle(Style.MAPBOX_STREETS)
  }

  override fun onStart() {
    log.trace("onStart()")
    super.onStart()
    mapView.onStart()
  }

  override fun onResume() {
    log.trace("onResume()")
    super.onResume()
    mapView.onResume()
  }

  override fun onPause() {
    log.trace("onPause()")
    super.onPause()
    mapView.onPause()
  }

  override fun onStop() {
    log.trace("onStop()")
    super.onStop()
    mapView.onStop()
  }

  override fun onLowMemory() {
    log.trace("onLowMemory()")
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onDestroy() {
    log.trace("onDestroy()")
    super.onDestroy()
    mapView.onDestroy()
  }

  override fun onSaveInstanceState(outState: Bundle) {
    log.trace("onSaveInstanceState()")
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }

}

private val log = org.slf4j.LoggerFactory.getLogger(MapboxFragment::class.java)
