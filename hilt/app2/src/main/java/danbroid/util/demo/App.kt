package danbroid.util.demo

import android.app.Application
import com.example.dagger.CoffeeShop
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {


  override fun onCreate() {
    super.onCreate()

    val coffeeShop: CoffeeShop = com.example.dagger.DaggerCoffeeShop.builder().build()
    coffeeShop.maker().brew()
    log.warn("got coffeeshop: $coffeeShop")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(App::class.java)
