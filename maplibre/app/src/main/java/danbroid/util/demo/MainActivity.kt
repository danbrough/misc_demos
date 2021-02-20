package danbroid.util.demo

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import danbroid.util.demo.content.rootContent
import danbroid.util.menu.MenuActivity
import danbroid.util.menu.MenuItemBuilder

class MainActivity : MenuActivity(){
  override fun createNavGraph(navController: NavController) =
      navController.createDemoNavGraph(this)

  val content by lazy {
    rootContent(this)
  }
  override fun getRootMenu() =content
}

private val log = org.slf4j.LoggerFactory.getLogger(MainActivity::class.java)
