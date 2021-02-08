package danbroid.util.demo.ui
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class HomeModel : ViewModel() {
  private val _s = MutableLiveData<String>()

  fun test1() {
    _s.postValue("Message from test1() at  date is ${Date()}")
  }

  init {
    viewModelScope.launch(Dispatchers.IO) {
      var c = 1
      while (true) {
        delay(1000)
        log.trace("emittin gmsg")
        _s.postValue("${c++}: The date is ${Date()}")
      }
    }
  }

  val msg = _s
  override fun onCleared() {
    log.info("onCleared()")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(HomeModel::class.java)
