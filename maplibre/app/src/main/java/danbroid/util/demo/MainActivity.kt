package danbroid.util.demo

import androidx.navigation.NavController
import danbroid.util.demo.content.rootContent
import danbroid.util.menu.MenuActivity
import danbroid.util.menu.MenuItemBuilder

class MainActivity : MenuActivity() {

  private val menuContent by lazy {
    rootContent(this)
  }

  override fun createNavGraph(navController: NavController) = navController.createDemoNavGraph(this)
  override fun getRootMenu(): MenuItemBuilder = menuContent


}