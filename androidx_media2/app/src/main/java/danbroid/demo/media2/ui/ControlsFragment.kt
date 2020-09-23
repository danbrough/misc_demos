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
    val audioClient = model.client
    btn_play_pause.setOnClickListener {
      audioClient.togglePause()
    }


    btn_prev.setOnClickListener {
      audioClient.skipToPrev()
    }

    btn_next.setOnClickListener {
      audioClient.skipToNext()
    }

    progress_bar.visibility = View.GONE

    audioClient.connected.observe(viewLifecycleOwner) {
      log.trace("connected $it")
    }

    audioClient.pauseEnabled.observe(viewLifecycleOwner) {
      log.trace("pauseEnabled: $it")
      btn_play_pause.setImageResource(if (it) R.drawable.ic_pause else R.drawable.ic_play)
    }

    audioClient.hasNext.observe(viewLifecycleOwner) {
      btn_next.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }

    audioClient.hasPrevious.observe(viewLifecycleOwner) {
      btn_prev.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }

    audioClient.metadata.observe(viewLifecycleOwner) {
      it?.also {
        title.text = it.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
        subtitle.text = it.getText(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
      } ?: run {
        title.text = ""
        subtitle.text = ""
      }
    }

    audioClient.currentItem.observe(viewLifecycleOwner) {
      buttons.visibility = if (it != null) View.VISIBLE else View.GONE
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    log.warn("onAttach view: $view")
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(ControlsFragment::class.java)
