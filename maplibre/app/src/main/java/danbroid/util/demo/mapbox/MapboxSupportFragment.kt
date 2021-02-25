package danbroid.util.demo.mapbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import danbroid.util.demo.R
import danbroid.util.demo.utils.MapConstants
import danbroid.util.demo.utils.setLocation

open class MapboxSupportFragment : MapboxBaseFragment() {


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    return inflater.inflate(R.layout.fragment_mapbox_support, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_container) as SupportMapFragment
    mapFragment.getMapAsync {
      mapboxMap = it
      onMapReady()
    }
  }


  override fun onMapReady() {
    log.debug("onMapReady()")
    mapboxMap.setLocation(MapConstants.LATLNG_HOME)
    mapboxMap.setStyle(Style.MAPBOX_STREETS)
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MapboxSupportFragment::class.java)
