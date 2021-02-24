package danbroid.util.demo.utils

import android.content.Context
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.RasterLayer
import com.mapbox.mapboxsdk.style.sources.RasterSource
import com.mapbox.mapboxsdk.style.sources.TileSet
import danbroid.util.demo.BuildConfig
import danbroid.util.misc.SingletonHolder
import org.slf4j.LoggerFactory

object MapConstants {
  val LATLNG_HOME = LatLng(-41.308618, 174.769413)

  val StyleOSM: Style.Builder by lazy {
    val tileSet = TileSet(
        "1.0.0",
        "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png",
        "https://b.tile.openstreetmap.org/{z}/{x}/{y}.png",
        "https://c.tile.openstreetmap.org/{z}/{x}/{y}.png",
    ).also {
      it.maxZoom = 20f
      it.setCenter(LATLNG_HOME)
    }
    val source = RasterSource("osm", tileSet, 256)


    val layer = RasterLayer("osm", "osm")

    Style.Builder()
        .withSource(source).withLayer(layer)
  }

}

fun MapboxMap.setLocation(latLng: LatLng) {
  cameraPosition = CameraPosition.Builder()
      .target(latLng)
      .zoom(14.0)
      .build()
}

private object _maps

private val log = LoggerFactory.getLogger(_maps::class.java)

private class MapBoxAPIWithKey(context: Context) {
  init {
    log.error("INIT MAP BOX API WITH KEY")
  }

  val mapbox = Mapbox.getInstance(context, BuildConfig.MAPBOX_TEST_TOKEN)


  companion object : SingletonHolder<MapBoxAPIWithKey, Context>(::MapBoxAPIWithKey)
}

private class MapBoxAPIWithNoKey(context: Context) {
  init {
    log.error("INIT MAP BOX API WITH NO KEY")
  }

  val mapbox = Mapbox.getInstance(context, null)


  companion object : SingletonHolder<MapBoxAPIWithNoKey, Context>(::MapBoxAPIWithNoKey)
}


fun Context.initMapBox(requireKey: Boolean): Mapbox =
    if (requireKey) MapBoxAPIWithKey.getInstance(this).mapbox
    else MapBoxAPIWithNoKey.getInstance(this).mapbox
