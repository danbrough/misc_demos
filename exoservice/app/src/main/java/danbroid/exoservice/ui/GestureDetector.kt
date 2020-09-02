package danbroid.exoservice.ui

import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

open class GestureDetector(
  context: Context,
  initializer: (GestureDetectorCompat.() -> Unit)? = null
) : android.view.GestureDetector.OnGestureListener, View.OnTouchListener {


  val detector = GestureDetectorCompat(context, this).also {
    initializer?.invoke(it)
  }

  override fun onTouch(v: View, event: MotionEvent): Boolean {

    if (detector.onTouchEvent(event)) {
      log.warn("detector.onTouchEvent(event) returned true")
      return true
    }

    log.warn("detector.onTouchEvent(event) returned false")

    if (event.action == MotionEvent.ACTION_DOWN)
      return v.performClick()

    return false
  }

  companion object {
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100
  }

  open fun onSwipeRight(): Boolean {
    log.info("onSwipeRight()")
    return false
  }

  open fun onSwipeLeft(): Boolean {
    log.info("onSwipeLeft()")
    return false
  }

  open fun onSwipeTop(): Boolean {
    log.info("onSwipeTop()")
    return false
  }

  open fun onSwipeBottom(): Boolean {
    log.info("onSwipeBottom()")
    return false
  }


  override fun onShowPress(e: MotionEvent?) {
  }

  override fun onSingleTapUp(e: MotionEvent?) = false

  override fun onDown(e: MotionEvent?) = false

  override fun onScroll(
    e1: MotionEvent?,
    e2: MotionEvent?,
    distanceX: Float,
    distanceY: Float
  ) = true

  override fun onLongPress(e: MotionEvent?) {
  }

  override fun onFling(
    e1: MotionEvent?,
    e2: MotionEvent?,
    velocityX: Float,
    velocityY: Float
  ): Boolean {
    log.trace("onFling() e1:$e1 e2:$e2")
    if (e1 == null || e2 == null) return true

    try {
      val diffY = e2.y - e1.y
      val diffX = e2.x - e1.x
      if (Math.abs(diffX) > Math.abs(diffY)) {
        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
          if (diffX > 0) {
            return onSwipeRight()
          } else {
            return onSwipeLeft()
          }

        }
      } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
        if (diffY > 0) {
          return onSwipeBottom()
        } else {
          return onSwipeTop()
        }

      }
    } catch (err: Exception) {
      log.error(err.message, err)
    }

    return false
  }


}

private val log = org.slf4j.LoggerFactory.getLogger(GestureDetector::class.java)

