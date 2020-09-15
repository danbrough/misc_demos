package danbroid.demo.media2.ui

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadata
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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

  @SuppressLint("ResourceType")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")
    super.onViewCreated(view, savedInstanceState)

    btn_play_pause.setOnClickListener {
      model.client.togglePause()
    }


    btn_prev.setOnClickListener {
      model.client.skipToPrev()
    }

    btn_next.setOnClickListener {
      model.client.skipToNext()
    }

    model.client.connected.observe(viewLifecycleOwner) {
      log.trace("connected $it")
    }

    model.client.pauseEnabled.observe(viewLifecycleOwner) {
      log.trace("pauseEnabled: $it")
      btn_play_pause.setImageResource(if (it) R.drawable.ic_pause else R.drawable.ic_play)
    }

    model.client.hasNext.observe(viewLifecycleOwner) {
      btn_next.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }

    model.client.hasPrevious.observe(viewLifecycleOwner) {
      btn_prev.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }

    model.client.metadata.observe(viewLifecycleOwner) {
      it?.also {
        title.text = it.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
        log.trace("updated title to ${title.text}")
      } ?: run {
        title.text = ""
      }

    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    log.warn("onAttach view: $view")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(ControlsFragment::class.java)
