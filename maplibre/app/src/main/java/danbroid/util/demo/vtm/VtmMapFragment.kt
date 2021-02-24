package danbroid.util.demo.vtm

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import danbroid.util.demo.R
import org.oscim.android.MapPreferences
import org.oscim.android.MapView
import org.oscim.layers.tile.vector.VectorTileLayer
import org.oscim.map.Map
import org.oscim.renderer.MapRenderer
import org.oscim.theme.VtmThemes
import org.oscim.tiling.TileSource
import org.oscim.tiling.source.OkHttpEngine
import org.oscim.tiling.source.mvt.MapzenMvtTileSource

open class VtmMapFragment : Fragment(R.layout.fragment_vtm_map) {
  protected lateinit var tileSource: TileSource
  protected lateinit var mapPrefs: MapPreferences
  protected lateinit var mapView: MapView
  protected lateinit var map: Map

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    MapRenderer.setBackgroundColor(-0x888889)

    mapView = view.findViewById(R.id.mapView)
    mapView.debugFlags = MapView.DEBUG_CHECK_GL_ERROR or MapView.DEBUG_LOG_GL_CALLS
    map = mapView.map()


/*
    map.setTheme(object : ThemeFile {
      override fun getRenderThemeAsStream(): InputStream =
          requireContext().assets.open("map_theme.xml")

      override fun isMapsforgeTheme() = false
      override fun getMenuCallback() = null
      override fun getRelativePathPrefix() = null

      override fun setMenuCallback(menuCallback: XmlRenderThemeMenuCallback?) = Unit
    })
*/


    map.eventLayer.apply {
      enableRotation(false)
      enableTilt(false)
      enableZoom(true)
      enableMove(true)
    }

    mapPrefs = MapPreferences(VtmMapFragment::javaClass.name, context)


    /*tileSource = OSciMap4TileSource.builder()
        .httpFactory(OkHttpEngine.OkHttpFactory())
        .build()*/

    tileSource = MapzenMvtTileSource.builder()
        .apiKey(getString(R.string.mapboxToken))
        .httpFactory(OkHttpEngine.OkHttpFactory()).build()

    //tileSource = DefaultSources.OPENSTREETMAP.build()
    val mBaseLayer = map.layers().add(VectorTileLayer(map, tileSource))
    map.setTheme(VtmThemes.DEFAULT)


/*    val tileCache = TileCache(context, null, "tile.db").apply {
      setCacheSize(10 * 1024 * 1024)
    }

    tileSource.setCache(tileCache)


    val baseLayer = map.setBaseMap(tileSource)*/

/*    val groupLayer = GroupLayer(map)
    val mBuildingLayer = BuildingLayer(map, mBaseLayer, false, true)
    groupLayer.layers.add(mBuildingLayer)
    groupLayer.layers.add(LabelLayer(map, mBaseLayer))
    map.layers().add(groupLayer)

    val mapScaleBar = DefaultMapScaleBar(map)
    mapScaleBar.scaleBarMode = DefaultMapScaleBar.ScaleBarMode.BOTH
    mapScaleBar.distanceUnitAdapter = MetricUnitAdapter.INSTANCE
    mapScaleBar.secondaryDistanceUnitAdapter = ImperialUnitAdapter.INSTANCE
    mapScaleBar.scaleBarPosition = MapScaleBar.ScaleBarPosition.BOTTOM_LEFT

    val mapScaleBarLayer = MapScaleBarLayer(map, mapScaleBar)
    val renderer = mapScaleBarLayer.renderer
    renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
    renderer.setOffset(5 * CanvasAdapter.getScale(), 0f)
    map.layers().add(mapScaleBarLayer)*/
  }

  override fun onResume() {
    super.onResume()
    mapPrefs.load(map)
    mapView.onResume()
  }

  override fun onPause() {
    mapPrefs.save(map)
    mapView.onPause()
    super.onPause()
  }

  override fun onDestroy() {

    mapView.onDestroy()
    super.onDestroy()
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(VtmMapFragment::class.java)
