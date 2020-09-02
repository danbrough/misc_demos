package danbroid.exoservice.ui.bottomcontrols

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.map
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import danbroid.exoservice.R
import danbroid.exoservice.player
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_fadeswitch.*

class FadeSwitchFragment : Fragment() {

  lateinit var behaviour: BottomSheetBehavior<View>
  var peekHeight: Int = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.fragment_fadeswitch, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    log.info("onViewCreated() $savedInstanceState")
    firstFragment = childFragmentManager.findFragmentById(R.id.first)
    secondFragment = childFragmentManager.findFragmentById(R.id.second)

    peekHeight = context!!.resources.getDimensionPixelSize(R.dimen.bottom_controls_height)
    log.error("PEEK HEIGHT: $peekHeight")
  }

  private fun onSlide(amount: Float) {
    //log.trace("onSlide() $amount")
    createFragments(true, true)
    first.alpha = 1 - amount
    second.alpha = amount
  }

  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    log.warn("onViewStateRestored() $savedInstanceState")
    super.onViewStateRestored(savedInstanceState)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    log.warn("onSaveInstanceState()")
    super.onSaveInstanceState(outState)
    outState.putInt("state", behaviour.state)
  }

  private fun onStateChanged(state: Int) {
    when (state) {
      BottomSheetBehavior.STATE_COLLAPSED -> {
        log.trace("STATE_COLLAPSED")
        second.alpha = 0f
        createFragments(true, false)
        (activity!!.nav_host_fragment!!.view!!.layoutParams as ViewGroup.MarginLayoutParams).also {
          log.error("Setting nav_host fragment margin to $peekHeight")
          it.bottomMargin = peekHeight
        }
      }
      BottomSheetBehavior.STATE_HIDDEN -> {
        log.trace("STATE_HIDDEN")
        createFragments(false, false)
        (activity!!.nav_host_fragment!!.view!!.layoutParams as ViewGroup.MarginLayoutParams).also {
          log.error("Setting nav_host fragment margin to 0")
          it.bottomMargin = 0
          activity!!.nav_host_fragment!!.view!!.layoutParams = it
        }
      }
      BottomSheetBehavior.STATE_EXPANDED -> {
        log.trace("STATE_EXPANDED")
        first.alpha = 0f
        createFragments(false, true)
      }
      BottomSheetBehavior.STATE_HALF_EXPANDED -> {
        log.trace("STATE_HALF_EXPANDED")
        //createFragments(true, true)
      }
      BottomSheetBehavior.STATE_SETTLING -> {
        log.trace("STATE_SETTLING")
        // createFragments(true, true)
      }
      BottomSheetBehavior.STATE_DRAGGING -> {
        log.trace("STATE_DRAGGING")
        createFragments(true, true)
      }
    }
  }


  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    behaviour = BottomSheetBehavior.from(requireView())

    behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

      override fun onSlide(view: View, amount: Float) {
        this@FadeSwitchFragment.onSlide(amount)
      }

      override fun onStateChanged(p0: View, p1: Int) {
        this@FadeSwitchFragment.onStateChanged(p1)
      }
    })

    requireContext().player.playbackQueue.map { it.queue.isNullOrEmpty() }
      .observe(viewLifecycleOwner) { empty ->
        log.warn("PLAYLIST EMPTY: $empty current state: ${behaviour.state}")
        behaviour.state =
          if (empty) BottomSheetBehavior.STATE_HIDDEN else BottomSheetBehavior.STATE_COLLAPSED
      }



    savedInstanceState?.getInt("state")?.also {
      log.trace("setting behaviour.state to $it")
      behaviour.state = it
      return
    }

    behaviour.state = BottomSheetBehavior.STATE_HIDDEN


    onStateChanged(behaviour.state)


  }

  var firstFragment: Fragment? = null
  var secondFragment: Fragment? = null

  fun createFragments(first: Boolean, second: Boolean) {
    log.trace("createFragments() $first $second")
    val modifyFirst = first == (firstFragment == null)
    val modifySecond = second == (secondFragment == null)
    if (modifyFirst || modifySecond)
      childFragmentManager.commit {
        if (modifyFirst) {
          if (first)
            replace(R.id.first, createFirst())
          else
            remove(firstFragment!!).also {
              firstFragment = null
            }
        }
        if (modifySecond) {
          if (second)
            replace(R.id.second, createSecond())
          else
            remove(secondFragment!!).also {
              secondFragment = null
            }
        }
      }
  }

  fun createFirst() = BottomControlsFragment().also {
    firstFragment = it
  }

  fun createSecond() = FullControlsFragment().also {
    secondFragment = it
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(FadeSwitchFragment::class.java)
