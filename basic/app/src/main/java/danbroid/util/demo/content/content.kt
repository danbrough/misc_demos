package danbroid.util.demo.content

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import danbroid.util.demo.DemoNavGraph
import danbroid.util.demo.R
import danbroid.util.demo.URI_CONTENT_PREFIX
import danbroid.util.menu.*
import danbroid.util.menu.Icons.iconicsIcon
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.coroutines.suspendCoroutine

private val log = LoggerFactory.getLogger("danbroid.util.demo.content")

@ExperimentalCoroutinesApi
fun rootContent(context: Context) = context.rootMenu<MenuItemBuilder> {
  id = URI_CONTENT_PREFIX
  titleID = R.string.app_name

  menu {
    title = "Handle Long click"
    onClick = {
    }
    onLongClick = {
      Toast.makeText(context, "Long click handled", Toast.LENGTH_SHORT).show()
    }
  }


}
