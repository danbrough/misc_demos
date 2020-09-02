package danbroid.exoservice.ui.bottomcontrols

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HideWithBottomSheetBehaviour : AppBarLayout.ScrollingViewBehavior {

  private var childStartY =
    UNDEFINED

  constructor() {}

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View) =
    getBottomSheetBehavior(dependency) != null


  override fun onDependentViewChanged(
    parent: CoordinatorLayout,
    child: View,
    dependency: View
  ) =
    getBottomSheetBehavior(dependency)?.also {
      var slideOffset = getSlideOffset(parent, dependency, it)

      if (slideOffset < 0) slideOffset = 0f

      child.alpha = 1 - slideOffset

      if (childStartY == UNDEFINED) {
        childStartY = child.y
      }

      val childHeight = child.measuredHeight
      val childY = childStartY - childHeight * slideOffset
      child.y = childY

    }.let {
      true
    }

  private fun getSlideOffset(
    parent: CoordinatorLayout,
    dependency: View,
    bottomSheetBehavior: BottomSheetBehavior<*>
  ): Float {
    val parentHeight = parent.measuredHeight
    val sheetY = dependency.y
    val peekHeight = bottomSheetBehavior.peekHeight
    val sheetHeight = dependency.height
    val collapseY = (parentHeight - peekHeight).toFloat()
    val expandY = (parentHeight - sheetHeight).toFloat()
    val deltaY = collapseY - expandY

    return (parentHeight.toFloat() - peekHeight.toFloat() - sheetY) / deltaY
  }

  private fun getBottomSheetBehavior(view: View): BottomSheetBehavior<*>? {
    val params = view.layoutParams as CoordinatorLayout.LayoutParams
    val behavior = params.behavior
    return if (behavior is BottomSheetBehavior<*>) {
      behavior
    } else null
  }

  companion object {
    private val UNDEFINED = java.lang.Float.MAX_VALUE

  }
}

