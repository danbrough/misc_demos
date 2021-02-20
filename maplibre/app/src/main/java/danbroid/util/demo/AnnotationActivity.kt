package danbroid.util.demo

import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.InfoWindow
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMapClickListener
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMapLongClickListener
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.sources.RasterSource
import com.mapbox.mapboxsdk.style.sources.Source
import com.mapbox.mapboxsdk.style.sources.TileSet
import java.text.DecimalFormat
import java.util.*

/*
// Define the map syle (OpenStreetMap raster tiles)
const style = {
  "version": 8,
	"sources": {
    "osm": {
			"type": "raster",
			"tiles": ["https://a.tile.openstreetmap.org/{z}/{x}/{y}.png"],
			"tileSize": 256,
      "attribution": "&copy; OpenStreetMap Contributors",
      "maxzoom": 19
    }
  },
  "layers": [
    {
      "id": "osm",
      "type": "raster",
      "source": "osm" // This must match the source key above
    }
  ]
};

// Initialise the map
const map = new mapboxgl.Map({
  container: 'map',
  style: style,
  center: [1, 15],
  zoom: 3
});

// Add the navigation control
map.addControl(new mapboxgl.NavigationControl());
 */
class AnnotationActivity : AppCompatActivity() {
  private val LAT_LON_FORMATTER = DecimalFormat("#.#####")

  private val STATE_MARKER_LIST = "markerList"

  val osmSource: Style.Builder by lazy {
    val tileSet = TileSet(
        "1.0.0",
        "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png",
        "https://b.tile.openstreetmap.org/{z}/{x}/{y}.png",
        "https://c.tile.openstreetmap.org/{z}/{x}/{y}.png",
    ).also {
      it.maxZoom = 19f
    }
    val source =  RasterSource("osm",tileSet,256)

    val layer = com.mapbox.mapboxsdk.style.layers.RasterLayer("osm","osm")
    Style.Builder().withSource(source).withLayer(layer)
  }

  lateinit var mapView: MapView
  lateinit var mapboxMap:MapboxMap
  private var markerList = ArrayList<MarkerOptions>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    log.info("onCreate()")
    val apiToken:String =  getString(R.string.access_token)
    Mapbox.getInstance(this,apiToken)
    setContentView(R.layout.activity_scalebar)
    mapView = findViewById<MapView>(R.id.mapView)
    mapView.onCreate(savedInstanceState)
    mapView.getMapAsync { map->
      mapboxMap = map
    //  map.setStyle(Style.LIGHT)
   //   val markerViewManager = MarkerViewManager(mapView, map)
      mapboxMap.addOnMapLongClickListener { point: LatLng? ->
        addMarker(point!!)
        false
      }

      mapboxMap.addOnMapClickListener { point: LatLng? ->
        addMarker(point!!)
        false
      }

      mapboxMap.setStyle(Style.MAPBOX_STREETS)

      if (savedInstanceState != null) {
        markerList = savedInstanceState.getParcelableArrayList(STATE_MARKER_LIST)!!
        if (markerList != null) {
          mapboxMap.addMarkers(markerList)
        }
      }
    }

    findViewById<View>(R.id.fabScaleWidget).setOnClickListener {
      resetMap()
    }
  }
  private fun addMarker(point: LatLng) {
    val pixel = mapboxMap.projection.toScreenLocation(point)
    val title: String = (LAT_LON_FORMATTER.format(point.latitude) + ", "
        +LAT_LON_FORMATTER.format(point.longitude))
    val snippet = "X = " + pixel.x.toInt() + ", Y = " + pixel.y.toInt()
    val marker = MarkerOptions()
        .position(point)
        .title(title)
        .snippet(snippet)
    markerList.add(marker)
    mapboxMap.addMarker(marker)
  }
  private fun resetMap() {
    if (mapboxMap == null) {
      return
    }
    markerList.clear()
    mapboxMap.removeAnnotations()
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
