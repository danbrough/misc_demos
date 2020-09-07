package danbroid.demo.media2.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.media2.session.MediaController
import androidx.media2.session.RemoteSessionPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import danbroid.demo.media2.R
import danbroid.demo.media2.model.AudioClientModel
import kotlinx.android.synthetic.main.fragment_bottom_controls.*

class ControlsFragment : BottomSheetDialogFragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.fragment_bottom_controls, container, false)

  val model by activityViewModels<AudioClientModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")
    super.onViewCreated(view, savedInstanceState)

    btn_play_pause.setOnClickListener {
      model.client.togglePause()
    }

    model.client.connected.observe(viewLifecycleOwner){
      log.trace("connected $it")
    }

    model.client.pauseEnabled.observe(viewLifecycleOwner){
      log.trace("pauseEnabled: $it")
      btn_play_pause.setImageResource(if (it) R.drawable.ic_media_pause_light else R.drawable.ic_media_play_light)
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    log.warn("onAttach view: $view")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(ControlsFragment::class.java)
