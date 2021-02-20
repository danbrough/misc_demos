package danbroid.util.demo.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import danbroid.util.demo.R
import danbroid.util.demo.URI_CONTENT_PREFIX
import danbroid.util.menu.navigateToMenuID


class HomeFragment : Fragment(R.layout.fragment_home) {

  val model by viewModels<HomeModel>()


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")

    model.msg.observe(viewLifecycleOwner) {
      log.warn("OBSERVED: $it")
    }

    view.findViewById<View>(R.id.test1).setOnClickListener {
      test1()
    }

    view.findViewById<View>(R.id.test2).setOnClickListener {
      findNavController().navigateToMenuID(URI_CONTENT_PREFIX)
    }
  }

  fun test1() {
    log.debug("test1()")

    model.test1()

  }
}

private val log = org.slf4j.LoggerFactory.getLogger(HomeFragment::class.java)
