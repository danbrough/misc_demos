package danbroid.demo.bottomsheet.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import danbroid.demo.bottomsheet.MainActivity
import danbroid.demo.bottomsheet.R

class BottomSheetFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.bottom_sheet_fragment, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

  }

  override fun onStart() {
    super.onStart()
    BottomSheetBehavior.from(requireView().parent as View).also {
      log.warn("behavior: $it")
    }

  }
}

private val log = org.slf4j.LoggerFactory.getLogger(BottomSheetFragment::class.java)
