/*
 * Copyright 2016-2017 devemux86
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

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import danbroid.util.demo.R
import okhttp3.Cache
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import org.oscim.android.MapPreferences
import org.oscim.android.MapView
import org.oscim.layers.TileGridLayer
import org.oscim.layers.tile.vector.VectorTileLayer
import org.oscim.map.Map
import org.oscim.tiling.TileSource
import org.oscim.tiling.source.OkHttpEngine
import org.oscim.tiling.source.UrlTileSource
import org.oscim.tiling.source.mvt.MapilionMvtTileSource
import java.io.File
import java.util.*

open class MapActivity constructor(protected val mContentView: Int = R.layout.activity_vtm_map) : AppCompatActivity() {
  companion object {
    const val MAPILION_API_KEY = "3b3d8353-0fb8-4513-bfe0-d620b2d77c45"
    const val USE_CACHE = true
  }

  protected var mBaseLayer: VectorTileLayer? = null
  protected var mTileSource: TileSource? = null
  protected var mGridLayer: TileGridLayer? = null

  protected val okHttpBuilder by lazy {
    val builder = OkHttpClient.Builder()
    if (MapilionMvtActivity.USE_CACHE) {
      // Cache the tiles into file system
      val cacheDirectory = File(externalCacheDir, "tiles")
      val cacheSize = 10 * 1024 * 1024 // 10 MB
      val cache = Cache(cacheDirectory, cacheSize.toLong())
      builder.cache(cache)
    }
    builder
  }

  protected val mapilionMvtTileSource: UrlTileSource by lazy {
    // https://github.com/square/okhttp/issues/4053
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
      val cipherSuites: MutableList<CipherSuite> = ArrayList()
      val modernTlsCipherSuites = ConnectionSpec.MODERN_TLS.cipherSuites()
      if (modernTlsCipherSuites != null) cipherSuites.addAll(modernTlsCipherSuites)
      cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA)
      cipherSuites.add(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
      val legacyTls = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
          .cipherSuites(*cipherSuites.toTypedArray())
          .build()
      okHttpBuilder.connectionSpecs(Arrays.asList(legacyTls, ConnectionSpec.CLEARTEXT))
    }
    val factory = OkHttpEngine.OkHttpFactory(okHttpBuilder)
    MapilionMvtTileSource.builder()
        .apiKey(MAPILION_API_KEY)
        .httpFactory(factory) //.locale("en")
        .build()
  }


  protected lateinit var mapView: MapView

  protected lateinit var map: Map

  protected lateinit var mapPrefs: MapPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(mContentView)
    title = javaClass.simpleName
    mapView = findViewById(R.id.mapView)
    map = mapView.map()
    mapPrefs = MapPreferences(MapActivity::class.java.name, this)
  }

  override fun onResume() {
    super.onResume()
    mapPrefs.load(mapView.map())
    mapView.onResume()
  }

  override fun onPause() {
    mapPrefs.save(mapView.map())
    mapView.onPause()
    super.onPause()
  }

  override fun onDestroy() {
    mapView.onDestroy()
    super.onDestroy()
  }
}