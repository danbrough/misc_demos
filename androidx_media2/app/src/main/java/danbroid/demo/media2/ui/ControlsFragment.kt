package danbroid.demo.media2.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.lifecycle.lifecycleScope
import androidx.media2.common.MediaMetadata
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import danbroid.demo.media2.R
import danbroid.demo.media2.databinding.FragmentBottomControlsBinding
import danbroid.demo.media2.model.audioClientModel
import danbroid.media.client.AudioClient
import danbroid.media.service.AudioService
import kotlinx.coroutines.flow.collect

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

  @ColorInt
  private var textColor: Int = Color.WHITE


  @SuppressLint("ResourceType")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")
    super.onViewCreated(view, savedInstanceState)
    val audioClient = audioClientModel.client



    binding.color1.setBackgroundColor(0)
    binding.color2.setBackgroundColor(0)
    binding.color3.setBackgroundColor(0)
    binding.color4.setBackgroundColor(0)
    binding.color5.setBackgroundColor(0)
    binding.color6.setBackgroundColor(0)


    binding.btnPlayPause.setOnClickListener {
      audioClient.togglePause()
    }

    binding.btnPrev.setOnClickListener {
      audioClient.skipToPrev()
    }

    binding.btnNext.setOnClickListener {
      audioClient.skipToNext()
    }

    textColor = binding.title.currentTextColor

    binding.progressBar.visibility = View.GONE

    lifecycleScope.launchWhenResumed {
      audioClient.connected.collect {
        log.trace("connected $it")
      }
    }



    lifecycleScope.launchWhenResumed {
      audioClient.queueState.collect {
        binding.btnNext.visibility = if (it.hasNext) View.VISIBLE else View.INVISIBLE
        binding.btnPrev.visibility = if (it.hasPrevious) View.VISIBLE else View.INVISIBLE
        if (it.size == 0) {
          binding.title.text = ""
          binding.subtitle.text = ""
        }
      }
    }

    lifecycleScope.launchWhenResumed {
      audioClient.playState.collect {
        binding.btnPlayPause.setImageResource(if (it != AudioClient.PlayerState.PAUSED) R.drawable.ic_pause else R.drawable.ic_play)
      }
    }



    lifecycleScope.launchWhenResumed {
      audioClient.metadata.collect {
        log.trace("metadata: $it")
        it?.also {
          it.extras?.also {

            if (it.containsKey(AudioService.MEDIA_METADATA_KEY_DARK_COLOR)) {
              binding.color1.setBackgroundColor(it.getInt(AudioService.MEDIA_METADATA_KEY_LIGHT_MUTED_COLOR, 0))
              binding.color2.setBackgroundColor(it.getInt(AudioService.MEDIA_METADATA_KEY_DARK_MUTED_COLOR, 0))
              binding.color3.setBackgroundColor(it.getInt(AudioService.MEDIA_METADATA_KEY_LIGHT_COLOR, 0))
              binding.color4.setBackgroundColor(it.getInt(AudioService.MEDIA_METADATA_KEY_DARK_COLOR, 0))
              binding.color5.setBackgroundColor(it.getInt(AudioService.MEDIA_METADATA_KEY_VIBRANT_COLOR, 0))
              binding.color6.setBackgroundColor(it.getInt(AudioService.MEDIA_METADATA_KEY_DOMINANT_COLOR, 0))

            }
          }

          it.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)?.also {
            log.error("FOUND MediaMetadata.METADATA_KEY_DISPLAY_ICON)")
          }
          binding.title.text = it.getText(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
          binding.subtitle.text = it.getText(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
        } ?: run {
          binding.title.text = ""
          binding.subtitle.text = ""
        }
      }
    }

    lifecycleScope.launchWhenResumed {
      audioClient.currentItem.collect { mediaItem ->
        log.warn("CURRENT ITEM: $mediaItem")
        binding.color1.setBackgroundColor(0)
        binding.color2.setBackgroundColor(0)
        binding.color3.setBackgroundColor(0)
        binding.color4.setBackgroundColor(0)
        binding.color5.setBackgroundColor(0)
        binding.color6.setBackgroundColor(0)

        binding.buttons.visibility = if (mediaItem != null) View.VISIBLE else View.GONE

        mediaItem?.metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON)?.also {
          log.error("FOUND MediaMetadata.METADATA_KEY_DISPLAY_ICON)")
        }
      }
    }

  }


}

private val log = danbroid.logging.getLog(ControlsFragment::class)