package danbroid.util.demo

import android.os.Bundle
import androidx.navigation.NavController
import dagger.hilt.android.AndroidEntryPoint
import danbroid.util.demo.content.rootContent
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : MenuActivity() {
  @Model
  @Inject
  @JvmField
  var model: String? = null

  @UserName
  @Inject
  @JvmField
  var name: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    log.info("onCreate() model:$model name:$name")
  }

  private val rootContent by lazy {
    rootContent(this)
  }

  override fun getRootMenu() = rootContent

  override fun createNavGraph(navController: NavController) =
      navController.createDemoNavGraph(this)


}

private val log = org.slf4j.LoggerFactory.getLogger(MainActivity::class.java)
