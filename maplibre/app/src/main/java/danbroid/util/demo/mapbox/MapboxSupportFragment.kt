package danbroid.util.demo.mapbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.maps.SupportMapFragment
import danbroid.util.demo.R
import danbroid.util.demo.utils.MapConstants
import danbroid.util.demo.utils.initMapBox
import danbroid.util.demo.utils.setLocation

open class MapboxSupportFragment : Fragment(R.layout.fragment_mapbox_support) {
  protected open val requireKey = true
  protected lateinit var mapboxMap: MapboxMap


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    requireContext().initMapBox(requireKey)
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val mapFragment = childFragmentManager.findFragmentById(R.id.fragment_container) as SupportMapFragment
    mapFragment.getMapAsync {
      mapboxMap = it
      onMapReady()
    }
  }

  protected open fun onMapReady() {
    log.debug("onMapReady()")
    mapboxMap.setLocation(MapConstants.LATLNG_HOME)
    mapboxMap.setStyle(Style.MAPBOX_STREETS)
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MapboxSupportFragment::class.java)
