package danbroid.util.demo

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TestModel @Inject constructor() : ViewModel() {

  var count = 0
}

private val log = org.slf4j.LoggerFactory.getLogger(TestModel::class.java)
