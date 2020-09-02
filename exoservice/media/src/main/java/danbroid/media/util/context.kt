package danbroid.media.util

import android.content.Context
import android.widget.Toast
import danbroid.media.BuildConfig

fun Context?.debugToast(msg: CharSequence) = if (BuildConfig.DEBUG && this != null) {
  Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
} else Unit

private val log = org.slf4j.LoggerFactory.getLogger("danbroid.exoservice.util")

