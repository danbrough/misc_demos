package danbroid.demo.media2.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.media2.common.MediaMetadata
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import danbroid.demo.media2.R
import danbroid.demo.media2.databinding.FragmentBottomControlsBinding
import danbroid.demo.media2.model.audioClientModel
import danbroid.media.client.AudioClient
import danbroid.media.service.TrackMetadata
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


  @SuppressLint("ResourceType")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.debug("onViewCreated()")
    super.onViewCreated(view, savedInstanceState)
    val audioClient = audioClientModel.client

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

    lifecycleScope.launchWhenResumed {
      audioClient.connected.collect {
        log.trace("connected $it")
      }
    }

    lifecycleScope.launchWhenResumed {
      audioClient.queueState.collect {
        binding.btnNext.visibility = if (it.hasNext) View.VISIBLE else View.INVISIBLE
        binding.btnPrev.visibility = if (it.hasPrevious) View.VISIBLE else View.INVISIBLE
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
            if (it.containsKey(TrackMetadata.MEDIA_METADATA_KEY_DARK_COLOR)) {
              val darkColor = it.getInt(TrackMetadata.MEDIA_METADATA_KEY_DARK_COLOR)
              log.info("FOUND DARK COLOR: %x".format(darkColor))
              binding.title.setBackgroundColor(darkColor)
              binding.title.setTextColor(it.getInt(TrackMetadata.MEDIA_METADATA_KEY_LIGHT_COLOR))
            }

            if (it.containsKey(TrackMetadata.MEDIA_METADATA_KEY_LIGHT_COLOR)) {
              val lightColor = it.getInt(TrackMetadata.MEDIA_METADATA_KEY_LIGHT_COLOR)
              log.info("FOUND LIGHT COLOR: %x".format(lightColor))
              binding.subtitle.setBackgroundColor(lightColor)
              binding.subtitle.setTextColor(it.getInt(TrackMetadata.MEDIA_METADATA_KEY_DARK_COLOR))
            }
          }
          it.getBitmap(TrackMetadata.MEDIA_METADATA_KEY_CACHED_ICON)?.also {
            log.error("FOUND BITMAP!!!!!!!!")
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
        binding.buttons.visibility = if (mediaItem != null) View.VISIBLE else View.GONE
        mediaItem?.metadata?.getBitmap(TrackMetadata.MEDIA_METADATA_KEY_CACHED_ICON)?.also {
          log.warn("FOUND ICON!")
        }
      }
    }

  }


}

private val log = danbroid.logging.getLog(ControlsFragment::class)