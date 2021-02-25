package danbroid.util.demo.mapbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import danbroid.util.demo.R

open class MapboxViewFragment : MapboxBaseFragment() {


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    return inflater.inflate(R.layout.fragment_map, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")
    mapView = view.findViewById(R.id.mapView)
    mapView.onCreate(savedInstanceState)
    mapView.getMapAsync {
      mapboxMap = it
      onMapReady()
    }
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

private val log = org.slf4j.LoggerFactory.getLogger(MapboxViewFragment::class.java)
