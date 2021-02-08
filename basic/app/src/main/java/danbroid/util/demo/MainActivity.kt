package danbroid.util.demo

import androidx.navigation.NavController
import danbroid.util.demo.content.rootContent
import danbroid.util.menu.MenuActivity


class MainActivity : MenuActivity() {

  private val rootContent by lazy {
    rootContent(this)
  }

  override fun getRootMenu() = rootContent

  override fun createNavGraph(navController: NavController) =
      navController.createDemoNavGraph(this)


}

//private val log = org.slf4j.LoggerFactory.getLogger(MainActivity::class.java)
