package danbroid.exoservice.ui

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.annotation.StyleRes
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView

open class SwipeRecyclerView(context: Context, attrs: AttributeSet, @StyleRes defStyle: Int) :
  RecyclerView(context, attrs, defStyle), GestureDetector.OnGestureListener {

  constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)


  override fun onTouchEvent(e: MotionEvent?): Boolean {
    if (gestureDetector.onTouchEvent(e)) return true
    else performClick()
    return true
  }

  override fun performClick(): Boolean {
    return super.performClick()
  }

  override fun onShowPress(e: MotionEvent?) {
  }

  override fun onSingleTapUp(e: MotionEvent?) = true

  var lastDownEvent: MotionEvent? = null

  override fun onDown(e: MotionEvent?): Boolean {
    lastDownEvent = e
    return true
  }

  override fun onFling(
    e1: MotionEvent?,
    e2: MotionEvent?,
    velocityX: Float,
    velocityY: Float
  ): Boolean {
    log.error("onFling() $e1 $e2  $velocityX,$velocityY")
    return true
  }

  override fun onScroll(
    e1: MotionEvent?,
    e2: MotionEvent?,
    distanceX: Float,
    distanceY: Float
  ) = true

  override fun onLongPress(e: MotionEvent?) {}

  protected val gestureDetector = GestureDetectorCompat(context, this)

}

private val log = org.slf4j.LoggerFactory.getLogger(SwipeRecyclerView::class.java)
