package danbroid.exoservice.ui.medialist

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import danbroid.exoservice.R
import danbroid.media.domain.MediaItem
import danbroid.util.resource.resolveDrawableURI
import danbroid.util.resource.setTint
import kotlinx.android.synthetic.main.mediaitem_fragment.view.*

class MediaListAdapter(val callbacks: MediaListCallbacks) :
  ListAdapter<MediaItem, MediaListAdapter.MediaItemViewHolder>(MediaItemDiffUtil()) {

  interface MediaListCallbacks {
    fun onItemClicked(holder: MediaItemViewHolder)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MediaItemViewHolder(
    LayoutInflater.from(parent.context).inflate(
      R.layout.mediaitem_fragment,
      parent,
      false
    )
  )

  override fun onBindViewHolder(holder: MediaItemViewHolder, position: Int) =
    holder.bind(getItem(position))

  inner class MediaItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var item: MediaItem? = null

    val context = itemView.context
    val iconSize = context.resources.getDimension(R.dimen.media_item_icon_size)
    val iconCornerRadius = (iconSize / 8).toInt()

    init {
      itemView.setOnClickListener {
        callbacks.onItemClicked(this)
      }
    }

    fun bind(item: MediaItem) {
      this.item = item
      itemView.title.text = item.title
      itemView.subtitle.text = item.subtitle
      itemView.state_in_playlist.visibility = View.GONE

      setIcon()
    }


    fun setIcon() {

      val image = item!!.imageURI
      val resID: Int = if (image == null)
        if (item!!.isBrowsable) R.drawable.ic_folder else R.drawable.ic_radio
      else
        image.resolveDrawableURI(context)

      itemView.icon.setTint(R.color.colorPrimaryLight)


      if (resID != 0) {

        itemView.icon.setImageDrawable(
          ResourcesCompat.getDrawable(
            context.resources,
            resID,
            context.theme
          )
        )
      } else {

        val placeholder = ResourcesCompat.getDrawable(
          context.resources,
          if (item!!.isBrowsable) R.drawable.ic_folder else R.drawable.ic_radio,
          context.theme
        )!!


        loadImage()
          .placeholder(placeholder)
          .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
              e: GlideException?,
              model: Any?,
              target: Target<Drawable>?,
              isFirstResource: Boolean
            ) = false

            override fun onResourceReady(
              resource: Drawable?,
              model: Any?,
              target: Target<Drawable>?,
              dataSource: DataSource?,
              isFirstResource: Boolean
            ): Boolean {
              itemView.icon.setTint(0)
              return false
            }

          })
          .into(itemView.icon)

      }
    }

    fun loadImage(): RequestBuilder<Drawable> {
/*      val circularProgressDrawable = CircularProgressDrawable(context)
      DrawableCompat.setTint(circularProgressDrawable,context.getThemeColour(R.attr.colorAccent))
circularProgressDrawable.arrowEnabled= true
      circularProgressDrawable.strokeWidth = 5f
      circularProgressDrawable.centerRadius = 50f
      circularProgressDrawable.start()*/
      return Glide.with(itemView.context).load(item!!.imageURI)
        //      .placeholder(circularProgressDrawable)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .transform(RoundedCorners(iconCornerRadius))
    }
  }
}


class MediaItemDiffUtil : DiffUtil.ItemCallback<MediaItem>() {
  override fun areItemsTheSame(oldItem: MediaItem, newItem: MediaItem) =
    oldItem.mediaID == newItem.mediaID

  override fun areContentsTheSame(oldItem: MediaItem, newItem: MediaItem) = true

}

private val log = org.slf4j.LoggerFactory.getLogger(MediaListAdapter::class.java)
