package danbroid.demo.media2.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import danbroid.demo.media2.R
import kotlinx.android.synthetic.main.fragment_bottom_controls.*

class ControlsFragment : BottomSheetDialogFragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.fragment_bottom_controls, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")
    super.onViewCreated(view, savedInstanceState)
  }

  override fun onStart() {
    super.onStart()
    log.warn("view: ${requireView()}")
    log.warn("parent: ${requireView().parent}")
    BottomSheetBehavior.from(requireView()).also {
      log.warn("behaviour: $it")
      it.isDraggable = false
      it.isHideable = false
      it.state = BottomSheetBehavior.STATE_EXPANDED
      it.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
          log.warn("onStateChanged() $newState")
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

      })
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    log.warn("onAttach view: $view")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(ControlsFragment::class.java)
