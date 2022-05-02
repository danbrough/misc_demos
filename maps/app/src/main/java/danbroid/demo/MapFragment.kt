package danbroid.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import danbroid.demo.databinding.FragmentMapBinding
import org.oscim.android.AndroidAssets
import org.oscim.backend.AssetAdapter
import org.oscim.backend.CanvasAdapter
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.renderer.GLViewport
import org.oscim.scalebar.DefaultMapScaleBar
import org.oscim.scalebar.MapScaleBarLayer
import org.oscim.scalebar.MetricUnitAdapter
import org.oscim.theme.IRenderTheme.ThemeException
import org.oscim.theme.ThemeFile
import org.oscim.theme.VtmThemes
import org.oscim.theme.XmlRenderThemeMenuCallback
import org.oscim.theme.XmlThemeResourceProvider
import org.oscim.tiling.source.mapfile.MapFileTileSource
import java.io.FileInputStream
import java.io.InputStream

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MapFragment : Fragment() {

  private var _binding: FragmentMapBinding? = null

  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ) = FragmentMapBinding.inflate(inflater, container, false).let {
    _binding = it
    it.root
  }

  //-41.30816630669619, 174.76997159992956
  @SuppressLint("ClickableViewAccessibility")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // MapsForgeTileSource.createInstance(this.getActivity().getApplication());
    //org.osmdroid.mapsforge.MapsForgeTileProvider;


    val mapView = binding.mapView
    val map = mapView.map()
    val tileSource = MapFileTileSource()


    /*
    XmlRenderTheme theme = null;
        try {
            theme = new AssetsRenderTheme(getContext().getApplicationContext(), "renderthemes/", "rendertheme-v4.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        fromFiles = MapsForgeTileSource.createFromFiles(maps, theme, "rendertheme-v4");
        forge = new MapsForgeTileProvider(
            new SimpleRegisterReceiver(getContext()),
            fromFiles, null);


        mMapView.setTileProvider(forge);
     */


    /*val fd = requireContext().assets.openFd("wgtn.map").fileDescriptor
    log.info("got fd")
    tileSource.setMapFileInputStream(FileInputStream(fd))*/
    tileSource.setMapFileInputStream(FileInputStream(requireContext().filesDir.resolve("wgtn.map")))
    val tileLayer = map.setBaseMap(tileSource)

    map.layers().add(BuildingLayer(map, tileLayer))
    map.layers().add(LabelLayer(map, tileLayer))


/*
    val mapScaleBar = DefaultMapScaleBar(mapView.map())
    val mapScaleBarLayer = MapScaleBarLayer(mapView.map(), mapScaleBar)
    mapScaleBarLayer.renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
    mapScaleBarLayer.renderer.setOffset(5 * CanvasAdapter.getScale(), 0f)
    mapView.map().layers().add(mapScaleBarLayer);
*/


    val mapScaleBar = DefaultMapScaleBar(map)
    mapScaleBar.scaleBarMode = DefaultMapScaleBar.ScaleBarMode.SINGLE
    mapScaleBar.distanceUnitAdapter = MetricUnitAdapter.INSTANCE
//    mapScaleBar.scaleBarPosition = MapScaleBar.ScaleBarPosition.BOTTOM_LEFT

    val mapScaleBarLayer = MapScaleBarLayer(map, mapScaleBar)
    val renderer = mapScaleBarLayer.renderer
    renderer.setPosition(GLViewport.Position.BOTTOM_LEFT)
    renderer.setOffset(5 * CanvasAdapter.getScale(), 50 * CanvasAdapter.getScale())
    map.layers().add(mapScaleBarLayer)

    AndroidAssets.init(requireContext())

    val theme = object : ThemeFile {
      override fun getMenuCallback(): XmlRenderThemeMenuCallback? {
        return null
      }

      override fun getRelativePathPrefix() = ""

      @Throws(ThemeException::class)
      override fun getRenderThemeAsStream(): InputStream? {
        return AssetAdapter.readFileAsStream("maptheme.xml")
      }

      override fun getResourceProvider(): XmlThemeResourceProvider? {
        return null
      }

      override fun isMapsforgeTheme(): Boolean {
        return false
      }

      override fun setMapsforgeTheme(mapsforgeTheme: Boolean) {}

      override fun setMenuCallback(menuCallback: XmlRenderThemeMenuCallback?) {}

      override fun setResourceProvider(resourceProvider: XmlThemeResourceProvider?) {}
    }

    map.setTheme(theme)
    // Note: this map position is specific to Berlin area
    map.setMapPosition(-41.30816630669619, 174.76997159992956, 1.shl(16).toDouble())
    /*   binding.buttonFirst.setOnClickListener {
         findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
       }*/

    val zoomControls = binding.zoomControls
    val btnZoomIn = binding.btnZoomIn
    val btnZoomOut = binding.btnZoomOut

    btnZoomIn.setOnClickListener {
      map.animator().animateZoom(1000L, 2.0, 0f, 0f)
    }
    btnZoomOut.setOnClickListener {
      map.animator().animateZoom(1000L, 0.5, 0f, 0f)
    }

    /*map.events.bind(org.oscim.map.Map.UpdateListener { e, mapPosition ->
      log.debug("event: $e post: $mapPosition")
    })*/


  }

  override fun onPause() {
    super.onPause()
    binding.mapView.onPause()
  }

  override fun onResume() {
    super.onResume()
    binding.mapView.onResume()
  }

  /*


            // Scale bar
            MapScaleBar mapScaleBar = new DefaultMapScaleBar(mapView.map());
            MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mapView.map(), mapScaleBar);
            mapScaleBarLayer.getRenderer().setPosition(GLViewport.Position.BOTTOM_LEFT);
            mapScaleBarLayer.getRenderer().setOffset(5 * CanvasAdapter.getScale(), 0);
            mapView.map().layers().add(mapScaleBarLayer);

            // Note: this map position is specific to Berlin area
            mapView.map().setMapPosition(52.517037, 13.38886, 1 << 12);
   */

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}