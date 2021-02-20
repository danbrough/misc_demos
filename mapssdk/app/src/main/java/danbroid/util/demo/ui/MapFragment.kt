package danbroid.util.demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import danbroid.util.demo.databinding.MapFragmentBinding

class MapFragment: Fragment() {
  private var _binding:MapFragmentBinding? = null
  private val binding:MapFragmentBinding
  get() = _binding!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")

  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
   = MapFragmentBinding.inflate(inflater,container,false).let {
    log.warn("inflated")
    _binding = it
     it.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MapFragment::class.java)
