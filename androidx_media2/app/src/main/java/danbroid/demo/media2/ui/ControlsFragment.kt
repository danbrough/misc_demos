package danbroid.demo.media2.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.media2.common.MediaMetadata
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import danbroid.demo.media2.R
import danbroid.demo.media2.databinding.FragmentBottomControlsBinding
import danbroid.demo.media2.model.AudioClientModel

class ControlsFragment : BottomSheetDialogFragment() {

  override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
  ) = FragmentBottomControlsBinding.inflate(inflater, container, false).let {
    _binding = it
    it.root
  }

  private var _binding: FragmentBottomControlsBinding? = null
  private val binding: FragmentBottomControlsBinding
    get() = _binding!!

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  val model by activityViewModels<AudioClientModel>()

  @SuppressLint("ResourceType")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")
    super.onViewCreated(view, savedInstanceState)
    val audioClient = model.client
    binding.btnPlayPause.setOnClickListener {
      audioClient.togglePause()
    }


    binding.btnPrev.setOnClickListener {
      audioClient.skipToPrev()
    }

    binding.btnNext.setOnClickListener {
      audioClient.skipToNext()
    }

    binding.progressBar.visibility = View.GONE

    audioClient.connected.observe(viewLifecycleOwner) {
      log.trace("connected $it")
    }

    audioClient.pauseEnabled.observe(viewLifecycleOwner) {
      log.trace("pauseEnabled: $it")
      binding.btnPlayPause.setImageResource(if (it) R.drawable.ic_pause else R.drawable.ic_play)
    }

    audioClient.hasNext.observe(viewLifecycleOwner) {
      binding.btnNext.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }

    audioClient.hasPrevious.observe(viewLifecycleOwner) {
      binding.btnPrev.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }

    audioClient.metadata.observe(viewLifecycleOwner) {
      it?.also {
        binding.title.text = it.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
        binding.subtitle.text = it.getText(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
      } ?: run {
        binding.title.text = ""
        binding.subtitle.text = ""
      }
    }

    audioClient.currentItem.observe(viewLifecycleOwner) {
      binding.buttons.visibility = if (it != null) View.VISIBLE else View.GONE
    }
  }


}

private val log = org.slf4j.LoggerFactory.getLogger(ControlsFragment::class.java)
