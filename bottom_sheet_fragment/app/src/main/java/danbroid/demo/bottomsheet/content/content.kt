package danbroid.demo.bottomsheet.content


import android.content.Context
import android.widget.Toast
import danbroid.demo.bottomsheet.R
import danbroid.util.menu.*

const val URI_CONTENT_ROOT = "demo://content"

fun rootContent(context: Context) = context.rootMenu<MenuItemBuilder> {

  id = URI_CONTENT_ROOT
  titleID = R.string.app_name

  menu {
    title = "First Menu"
    subtitle = "subtitle"
    onClick = {
      Toast.makeText(context, "Clicked the first menu", Toast.LENGTH_SHORT).show()
      false
    }
  }

  menu {
    id = "$URI_CONTENT_ROOT/second"
    title = "Second Menu"
   // onClick = promptToContinue

    menu {
      title = "Child of Second Menu"
      id = "$URI_CONTENT_ROOT/second/first"
    }
  }

/*
  menu {
    title = "Live Menu"
    isBrowsable = true
    tint = Color.BLUE
    liveChildren = liveChildrenProducer
  }
*/

  menu {
    title = "Another Menu1"
  }
  menu {
    title = "Another Menu2"
  }
  menu {
    title = "Another Menu3"
  }
  menu {
    title = "Another Menu4"
  }
  menu {
    title = "Another Menu5"
  }
  menu {
    title = "Another Menu6"
  }
}


/*
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
}*/
