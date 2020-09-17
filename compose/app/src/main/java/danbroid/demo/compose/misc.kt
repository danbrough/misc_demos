package danbroid.demo.compose

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import danbroid.demo.compose.ui.MyApplicationTheme


@Composable
fun SimpleTopAppBar(context: Context?) {
  TopAppBar(
    title = { Text("Simple TopAppBar") },
    navigationIcon = {
      IconButton(onClick = {
        Toast.makeText(context!!, "Clicked", Toast.LENGTH_SHORT).show()
      }) {
        Icon(Icons.Filled.Menu)
      }
    },
    actions = {
      // RowScope here, so these icons will be placed horizontally
      IconButton(onClick = { /* doSomething() */ }) {
        Icon(Icons.Filled.Favorite)
      }
      IconButton(onClick = { /* doSomething() */ }) {
        Icon(Icons.Filled.Favorite)
      }
    }
  )
}


@Composable
fun NewsStory(context: Context? = null) {
  Column {
    SimpleTopAppBar(context)

    val image = imageResource(R.drawable.header)
    Column(
      modifier = Modifier.padding(16.dp)
    ) {

      val imageModifier = Modifier
        .preferredHeightIn(minHeight = 280.dp)
        .fillMaxWidth()

      Image(
        image, modifier = imageModifier,
        contentScale = ContentScale.Crop
      )

      Spacer(modifier = Modifier.preferredHeight(16.dp))


      Text("A day in Shark Fin Cove Demo App")
      Text("Davenport, California")
      Text("December 2018", modifier = Modifier.fillMaxHeight())
    }
  }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  MyApplicationTheme(darkTheme = false) {
    NewsStory()
  }
}
