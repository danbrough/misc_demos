package danbroid.exoservice.ui.bottomcontrols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import danbroid.exoservice.R
import danbroid.exoservice.player
import danbroid.media.messages.PlayerState
import kotlinx.android.synthetic.main.fragment_bottomcontrols.*

class BottomControlsFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.fragment_bottomcontrols, container, false)


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    log.info("onViewCreated()")

    val player = context!!.player
    btn_next.visibility = View.INVISIBLE
    btn_prev.visibility = View.INVISIBLE
    btn_pause.visibility = View.INVISIBLE
    btn_stop.visibility = View.INVISIBLE
    loading.visibility = View.INVISIBLE
    title.text = ""
    subtitle.text = ""
    subtitle.isSelected = true

    player.playerState.observe(viewLifecycleOwner) {
      log.warn("playerStateChange: $it")
      btn_next.visibility = if (it.hasNext) View.VISIBLE else View.INVISIBLE
      btn_prev.visibility = if (it.hasPrev) View.VISIBLE else View.INVISIBLE

      btn_stop.visibility =
        if (it.isPlaying || it.state == PlayerState.READY) View.VISIBLE else View.GONE

      btn_pause.setImageResource(if (it.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }

    player.loading.observe(viewLifecycleOwner) {
      loading.visibility = if (it) View.VISIBLE else View.INVISIBLE
    }

    player.playbackQueue.observe(viewLifecycleOwner) {
      btn_pause.visibility = if (it.queue.isEmpty()) View.INVISIBLE else View.VISIBLE
    }

    player.playItem.observe(viewLifecycleOwner) {
      log.warn("playItem changed: $it")
      title.text = it?.title ?: ""
      subtitle.text = it?.subtitle ?: ""
    }

    btn_next.setOnClickListener { player.skipNext() }
    btn_prev.setOnClickListener { player.skipPrev() }
    btn_pause.setOnClickListener { player.togglePause() }
    btn_stop.setOnClickListener { player.stop() }

  }


  override fun onDestroy() {
    super.onDestroy()
    log.info("onDestroy()")
  }

}

private val log = org.slf4j.LoggerFactory.getLogger(BottomControlsFragment::class.java)

