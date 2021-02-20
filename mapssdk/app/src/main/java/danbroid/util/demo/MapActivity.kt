package danbroid.util.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style

class MapActivity : AppCompatActivity() {

  lateinit var mapView: MapView
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    log.info("onCreate()")
    Mapbox.getInstance(this, getString(R.string.access_token))
    setContentView(R.layout.map_fragment)
    mapView = findViewById<MapView>(R.id.mapView)
    mapView.onCreate(savedInstanceState)

    mapView.getMapAsync {

      it.setStyle(Style.DARK) {
// Map is set up and the style has loaded. Now you can add data or make other map adjustments.

      }
    }
  }


  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }


  // Add the mapView lifecycle to the activity's lifecycle methods
  override fun onResume() {
    super.onResume()
    mapView.onResume()
  }

  override fun onStart() {
    super.onStart()
    mapView.onStart()
  }

  override fun onStop() {
    super.onStop()
    mapView.onStop()
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }


}

private val log = org.slf4j.LoggerFactory.getLogger(MapActivity::class.java)
