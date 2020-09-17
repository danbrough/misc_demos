package danbroid.media.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import danbroid.util.resource.resolveDrawableURI
import java.io.IOException

class IconUtils(
  val context: Context,
  @ColorInt val iconTint: Int = Config.Notifications.notificationIconTint
) {

  companion object {
    var BITMAP_CONFIG = Bitmap.Config.ARGB_8888
  }


  @Throws(IOException::class)
  fun loadIcon(
    iconUri: String,
    defaultIcon: Bitmap? = null,
    callback: ((Bitmap) -> Unit)? = null
  ): Bitmap? {


    iconUri.resolveDrawableURI(context).also {
      if (it != 0) return drawableToBitmapIcon(it)
    }

    if (callback != null)
      Glide.with(context).asBitmap().load(iconUri).diskCacheStrategy(DiskCacheStrategy.DATA)
        //.transform(RoundedCorners(iconCornerRadius))
        .into(object : CustomTarget<Bitmap>(
          Config.Notifications.notificationIconWidth,
          Config.Notifications.notificationIconHeight
        ) {
          override fun onLoadCleared(placeholder: Drawable?) = Unit

          override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            callback.invoke(resource)
          }
        })


    return defaultIcon
  }


  fun loadIcon(
    mediaItem: Any?,
    defaultIcon: Bitmap? = null,
    callback: (Bitmap) -> Unit
  ): Bitmap? {
    log.trace("loadImage() $mediaItem")

/*
    mediaItem ?: let {
      log.error("mediaItem is null")
      return defaultIcon
    }


    mediaItem.extras?.also {
      if (it is Bitmap) return it
    }

    val imageURI = mediaItem.imageURI

    if (imageURI == null) {
      mediaItem.extras = defaultIcon
      return defaultIcon
    }


    mediaItem.extras = loadIcon(imageURI, defaultIcon) {
      mediaItem.extras = it
      callback.invoke(it)
    }
*/

    return defaultIcon
  }

  fun drawableToBitmapIcon(@DrawableRes resID: Int): Bitmap {
    val drawable = ResourcesCompat.getDrawable(context.resources, resID, context.theme)!!

    val bitmap = Bitmap.createBitmap(
      Config.Notifications.notificationIconWidth,
      Config.Notifications.notificationIconHeight,
      BITMAP_CONFIG
    )

    log.trace("drawing bitmap ..")
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)

    if (iconTint != 0)
      DrawableCompat.setTint(
        drawable,
        iconTint
      )
    drawable.draw(canvas)
    return bitmap
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(IconUtils::class.java)

