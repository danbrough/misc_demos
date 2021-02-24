/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2019 devemux86
 * Copyright 2019 Gustl22
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package danbroid.util.demo.vtm


import android.os.Bundle
import org.oscim.backend.CanvasAdapter
import org.oscim.layers.GroupLayer
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.VectorTileLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.renderer.GLViewport
import org.oscim.scalebar.*
import org.oscim.theme.VtmThemes

class SimpleMapActivity : MapActivity() {
  var mBuildingLayer: BuildingLayer? = null
  private var mShadow: Boolean = false


  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    createLayers()
  }

  /*
      val layer: VectorTileLayer = map.setBaseMap(mapilionMvtTileSource)
    map.setTheme(VtmThemes.OPENMAPTILES)

    // Hillshading
    val shadedTileSource: UrlTileSource = DefaultSources.MAPILION_HILLSHADE_2
        .apiKey(API_KEY)
        .httpFactory(OkHttpFactory(okHttpBuilder))
        .build()
    map.layers().add(BitmapTileLayer(map, shadedTileSource))
    map.layers().add(BuildingLayer(map, layer))
    map.layers().add(LabelLayer(map, layer))
   */
  fun createLayers() {
    
    val mBaseLayer = map.setBaseMap(mapilionMvtTileSource)
    /* val shadedTileSource: UrlTileSource = DefaultSources.MAPILION_HILLSHADE_2
         .apiKey(MAPILION_API_KEY)
         .httpFactory(OkHttpEngine.OkHttpFactory(okHttpBuilder))
         .build()*/
    map.layers().add(VectorTileLayer(map, mapilionMvtTileSource))

    map.setTheme(VtmThemes.OPENMAPTILES)

    val groupLayer = GroupLayer(map)
    mBuildingLayer = BuildingLayer(map, mBaseLayer, false, mShadow)
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
    renderer.setOffset(5 * CanvasAdapter.getScale(), 0.0f)
    map.layers().add(mapScaleBarLayer)
    //  map.setTheme(VtmThemes.DEFAULT)
  }

  /* fun runTheMonkey() {
     themes[0] = ThemeLoader.load(VtmThemes.DEFAULT)
     themes[1] = ThemeLoader.load(VtmThemes.OSMARENDER)
     themes[2] = ThemeLoader.load(VtmThemes.TRONRENDER)
     loooop(1)
   }*/

  // var themes = arrayOfNulls<IRenderTheme>(3)

/*  // Stress testing
  fun loooop(i: Int) {
    val time = (500 + Math.random() * 1000).toLong()
    mMapView.postDelayed(Runnable {
      mMapView.map().setTheme(themes[i])
      val p = MapPosition()
      if (i == 1) {
        mMapView.map().getMapPosition(p)
        p.setScale(4.0)
        mMapView.map().animator().animateTo(time, p)
      } else {
        //mMapView.map().setMapPosition(p);
        p.setScale((2 + (1 shl (Math.random() * 13).toInt())).toDouble())
        //p.setX((p.getX() + (Math.random() * 4 - 2) / p.getScale()));
        //p.setY((p.getY() + (Math.random() * 4 - 2) / p.getScale()));
        p.setX(MercatorProjection.longitudeToX(Math.random() * 180))
        p.setY(MercatorProjection.latitudeToY(Math.random() * 60))
        p.setTilt((Math.random() * 60).toFloat())
        p.setBearing((Math.random() * 360).toFloat())
        p.setRoll((Math.random() * 360).toFloat())
        //mMapView.map().setMapPosition(p);
        mMapView.map().animator().animateTo(time, p)
      }
      loooop((i + 1) % 2)
    }, time)
  }*/
}