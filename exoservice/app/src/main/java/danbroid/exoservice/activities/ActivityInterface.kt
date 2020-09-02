package danbroid.exoservice.activities

import com.google.android.material.snackbar.Snackbar
import danbroid.exoservice.ui.medialist.MediaListAdapter


interface ActivityInterface {
  fun onItemClicked(holder: MediaListAdapter.MediaItemViewHolder)

  fun showSnackbar(
    msg: CharSequence,
    length: Int = Snackbar.LENGTH_SHORT,
    onUndo: (() -> Unit)? = null
  )
}