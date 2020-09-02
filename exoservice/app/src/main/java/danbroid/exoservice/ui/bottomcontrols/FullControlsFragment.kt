package danbroid.exoservice.ui.bottomcontrols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import danbroid.exoservice.R

class FullControlsFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.fragment_full_controls, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.info("onViewCreated()")
  }

  override fun onDestroy() {
    super.onDestroy()
    log.info("onDestroy()")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(FullControlsFragment::class.java)

