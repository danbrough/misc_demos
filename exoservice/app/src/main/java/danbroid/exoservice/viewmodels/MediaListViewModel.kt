package danbroid.exoservice.viewmodels

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import danbroid.exoservice.content.MediaItemBuilder
import danbroid.exoservice.content.rootContent
import danbroid.media.domain.MediaItem
import kotlinx.coroutines.flow.map

typealias  UndoCallback = () -> Unit

class MediaListViewModel(context: Context, val mediaID: String) : ViewModel() {

  private val builder: MediaItemBuilder = rootContent(context).find(mediaID)
    ?: throw Exception("Invalid mediaID: $mediaID")

  init {
    log.trace("created model for $mediaID")
  }

  val item: LiveData<MediaItem> = builder.loadItem().asLiveData()

  val children: LiveData<List<MediaItem>> =
    builder.loadChildren().map { it.filter { !it.isHidden } }.asLiveData()

  override fun onCleared() {
    log.trace("onCleared() $mediaID")
  }

  fun removeItem(item: MediaItem): UndoCallback? {
    val index = children.value!!.indexOf(item)
    builder.removeItem?.invoke(item) ?: return null

    return builder.insertItem?.let { insert ->
      {
        insert.invoke(item, index)
      }
    }
  }

  fun insertItem(item: MediaItem, index: Int) {
    log.warn("insertItem() $index")
    builder.insertItem?.invoke(item, index)
  }

  class Factory(val fragment: Fragment, val mediaID: String) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
      return MediaListViewModel(fragment.context!!, mediaID) as T
    }
  }
}


private val log = org.slf4j.LoggerFactory.getLogger(MediaListViewModel::class.java)
