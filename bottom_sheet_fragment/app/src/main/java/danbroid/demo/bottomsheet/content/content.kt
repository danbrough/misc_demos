package danbroid.demo.bottomsheet.content


import android.graphics.Color
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import danbroid.demo.bottomsheet.R
import danbroid.util.menu.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

const val URI_CONTENT_ROOT = "demo://content"

val rootContent: MenuItemBuilder by lazy {
  rootMenu<MenuItemBuilder> {
    id = URI_CONTENT_ROOT
    titleID = R.string.app_name

    menu {
      title = "First Menu"
      subtitle = "subtitle"
      onClick = {
        Toast.makeText(context, "Clicked the first menu", Toast.LENGTH_SHORT).show()
      }
    }

    menu {
      id = "$URI_CONTENT_ROOT/second"
      title = "Second Menu"
      onClick = promptToContinue

      menu {
        title = "Child of Second Menu"
        id = "$URI_CONTENT_ROOT/second/first"
      }
    }

    menu {
      title = "Live Menu"
      isBrowsable = true
      tint = Color.BLUE
      liveChildren = liveChildrenProducer
    }


  }
}

private val liveChildrenProducer: LiveChildrenProducer = { ctx, id, item ->
  withContext(Dispatchers.Main) {
    Toast.makeText(ctx, "Loading the live menu", Toast.LENGTH_SHORT).show()
  }
  delay(1000)
  listOf(
    MenuItem(item!!.id + "/1", "Sub Menu1", "menu1"),
    MenuItem(item.id + "/2", "Sub Menu2", "menu2")
  )
}

private val promptToContinue: MenuItemClickHandler = { callback ->
  AlertDialog.Builder(context).apply {
    setTitle(android.R.string.dialog_alert_title)
    setMessage("Do you want to continue?")
    setPositiveButton(android.R.string.ok) { dialog, which ->
      callback(true)
    }
    setNegativeButton(android.R.string.cancel) { dialog, which ->
      callback(false)
    }
    show()
  }
}