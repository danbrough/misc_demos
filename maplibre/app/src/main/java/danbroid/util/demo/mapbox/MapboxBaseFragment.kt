package danbroid.util.demo.mapbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import danbroid.util.demo.R
import danbroid.util.demo.utils.MapConstants
import danbroid.util.demo.utils.initMapBox
import danbroid.util.demo.utils.setLocation

open class MapboxBaseFragment : Fragment(), PermissionsListener {
  private val permissionsManager by lazy { PermissionsManager(this) }
  protected lateinit var mapView: MapView

  protected lateinit var mapboxMap: MapboxMap
  protected open val requireApiKey = true


  @CallSuper
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    requireContext().initMapBox(requireApiKey)
    return null
  }

  open fun onMapReady() {
    mapboxMap.setLocation(MapConstants.LATLNG_HOME)
    mapboxMap.setStyle(Style.MAPBOX_STREETS)
  }


  @SuppressLint("MissingPermission")
  protected fun enableLocation(loadedMapStyle: Style) {
    log.info("enableLocation()")

    if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
      log.debug("creating location component..")

      // Create and customize the LocationComponent's options
      val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
          .trackingGesturesManagement(true)
          .accuracyColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
          .build()

      val locationComponentActivationOptions = LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
          .locationComponentOptions(customLocationComponentOptions)
          .build()

      // Get an instance of the LocationComponent and then adjust its settings
      mapboxMap.locationComponent.apply {

        // Activate the LocationComponent with options
        activateLocationComponent(locationComponentActivationOptions)

        // Enable to make the LocationComponent visible
        isLocationComponentEnabled = true

        // Set the LocationComponent's camera mode
        cameraMode = CameraMode.TRACKING

        // Set the LocationComponent's render mode
        renderMode = RenderMode.COMPASS
      }
    } else {
      //permissionsManager = PermissionsManager(this)
      log.debug("requesting location permissions..")
      permissionsManager.requestLocationPermissions(requireActivity())
    }

  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  override fun onExplanationNeeded(permissionsToExplain: List<String>) {
    Toast.makeText(requireContext(), "I need persmissions to access the current location", Toast.LENGTH_LONG).show()
  }

  override fun onPermissionResult(granted: Boolean) {
    log.debug("onPermissionResult() $granted")
    if (granted) {
      enableLocation(mapboxMap.style!!)
    } else {
      Toast.makeText(requireContext(), "Well ok then", Toast.LENGTH_LONG).show()
    }
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MapboxBaseFragment::class.java)
