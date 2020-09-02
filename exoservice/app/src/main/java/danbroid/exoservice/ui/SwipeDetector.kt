package danbroid.exoservice.ui

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

open class SwipeDetector(context: Context) : GestureDetector.OnGestureListener,
  View.OnTouchListener {


  companion object {
    var SWIPE_THRESHOLD = 100
    var SWIPE_VELOCITY_THRESHOLD = 100
  }

  val gestureDetector = GestureDetectorCompat(context, this)

  override fun onTouch(v: View, event: MotionEvent): Boolean {
    if (gestureDetector.onTouchEvent(event)) return true
    if (event.action == MotionEvent.ACTION_DOWN)
      v.performClick()
    return true
  }

  override fun onShowPress(e: MotionEvent?) {
  }

  override fun onSingleTapUp(e: MotionEvent?) = true

  override fun onDown(e: MotionEvent?) = true

  override fun onFling(
    e1: MotionEvent?,
    e2: MotionEvent?,
    velocityX: Float,
    velocityY: Float
  ): Boolean {

    log.trace("onFling() $e1,$e2  vX:$velocityX vY:$velocityY")
    if (e1 == null || e2 == null) return false

    val diffY = e2.y - e1.y
    val diffX = e2.x - e1.x

    log.trace("$diffX,$diffY velocity: $velocityX,$velocityY")
    return false
  }

  override fun onScroll(
    e1: MotionEvent?,
    e2: MotionEvent?,
    distanceX: Float,
    distanceY: Float
  ) = false

  override fun onLongPress(e: MotionEvent?) {
  }

}

private val log = org.slf4j.LoggerFactory.getLogger(SwipeDetector::class.java)

