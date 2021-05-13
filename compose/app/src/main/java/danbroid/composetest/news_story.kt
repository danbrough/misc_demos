package danbroid.composetest

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import danbroid.composetest.ui.theme.typography
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

@Preview(showBackground = true)
@Composable
fun NewsStory() {
  val headerImg: Painter = painterResource(R.drawable.header)

  Column(modifier = Modifier.padding(16.dp)) {
    val imageModifier = Modifier
      .height(180.dp)
      .fillMaxWidth().clip(shape = RoundedCornerShape(4.dp))


    Image(
      painterResource(R.drawable.header),
      "header image",
      modifier = imageModifier
    )


    Spacer(Modifier.height(16.dp))

    Text(
      "A day wandering through the sandhills " +
          "in Shark Fin Cove, and a few of the " +
          "sights I saw",
      style = typography.h6,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis
    )
    Text(
      "Davenport, California",
      style = typography.body2
    )
    Text(
      "December 2018",
      style = typography.body2
    )


  }


}


class HelloViewModel : ViewModel() {

  init {
    log.info("Created helloview model")
  }

  // LiveData holds state which is observed by the UI
  // (state flows down from ViewModel)
  val name = MutableLiveData("initial name")


  val counter = MutableLiveData<Int>(0)


  // onNameChanged is an event we're defining that the UI can invoke
  // (events flow up from UI)
  fun onNameChanged(newName: String) {
    name.value = newName
  }
}

@Composable
fun TestContent1(helloViewModel: HelloViewModel = viewModel()) {
  Column(modifier = Modifier.padding(horizontal = 8.dp)) {
    Text("Hello from here at  ${Date()}")

    val name: State<String> = helloViewModel.name.observeAsState("")

    Column() {
      Text(text = name.value)
      TextField(
        readOnly = false,
        value = name.value,
        onValueChange = { helloViewModel.onNameChanged(it) },
        label = { Text("Name") }
      )

      HelloInput("test", name) {
        log.warn("TEXT: $it")
        helloViewModel.name.value = it
      }

      Counter(helloViewModel)

    }
  }
}


class TestModel : ViewModel() {
  val _counter = MutableStateFlow(123)
  val counter = _counter.asStateFlow()
  val _liveCounter = MutableLiveData(123)
  val liveCounter = _liveCounter

}


@Composable
fun TestContent2(model: TestModel = viewModel()) {

  Column(modifier = Modifier.padding(horizontal = 8.dp)) {
    Text("Hello from here at  ${Date()}")

    Button(
      onClick = {
        log.info("clicked button counter: ${model._counter.value}")
        model._counter.value = model._counter.value + 1
      }) {
      Text("Test Button ${model.counter.collectAsState().value}")
    }
  }
}

@Composable
fun TestText(_text: String = "The date is ${Date()}") {
  val text = remember {
    _text
  }
  Text(text)
}

@Composable
fun Counter(helloViewModel: HelloViewModel = viewModel()) {
  log.info("Counter()")
  val counter = helloViewModel.counter.observeAsState()

  Button(

    onClick = {
      log.debug("clicked button")
      helloViewModel.counter.value = helloViewModel.counter.value!! + 1
      helloViewModel.name.value = "Date: ${Date()}"
    }) {
    Text("I've been clicked ${counter.value} times")
  }
}

@Composable
fun HelloInput(
  name: String, /* state */
  text: State<String>,
  onNameChange: (String) -> Unit /* event */
) {
  Column {
    Text(name)
    TextField(
      value = text.value,
      onValueChange = onNameChange,
      label = { Text("Name") }
    )
  }
}

private val log = org.slf4j.LoggerFactory.getLogger("news_story.kt")