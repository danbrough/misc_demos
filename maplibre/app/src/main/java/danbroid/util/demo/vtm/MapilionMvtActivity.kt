/*
 * Copyright 2018-2020 devemux86
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
import danbroid.util.demo.utils.MapConstants.LATLNG_HOME
import org.oscim.layers.tile.bitmap.BitmapTileLayer
import org.oscim.layers.tile.buildings.BuildingLayer
import org.oscim.layers.tile.vector.VectorTileLayer
import org.oscim.layers.tile.vector.labeling.LabelLayer
import org.oscim.theme.VtmThemes
import org.oscim.tiling.source.OkHttpEngine.OkHttpFactory
import org.oscim.tiling.source.UrlTileSource
import org.oscim.tiling.source.bitmap.DefaultSources


class MapilionMvtActivity : MapActivity() {


  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

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
  }

  override fun onResume() {
    super.onResume()
    map.setMapPosition(LATLNG_HOME.latitude, LATLNG_HOME.longitude, 42.0)

  }

  companion object {
    // Metered API key for demonstration purposes
    private const val API_KEY = "3b3d8353-0fb8-4513-bfe0-d620b2d77c45"
    const val USE_CACHE = true
  }
}