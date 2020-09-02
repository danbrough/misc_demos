package danbroid.exoservice.ui.medialist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import danbroid.exoservice.R
import danbroid.exoservice.activities.ActivityInterface
import danbroid.exoservice.content.URI_CONTENT_PLAYBACK_QUEUE
import danbroid.exoservice.player
import danbroid.exoservice.viewmodels.AudioPlayer
import danbroid.exoservice.viewmodels.MediaListViewModel
import kotlinx.android.synthetic.main.fragment_medialist.*

class MediaListFragment : Fragment() {

  val args: MediaListFragmentArgs by navArgs()

  val model: MediaListViewModel by viewModels {
    MediaListViewModel.Factory(this, args.mediaID)
  }

  val player: AudioPlayer by lazy {
    context!!.player
  }


  val activityInterface: ActivityInterface
    get() = activity as ActivityInterface

  lateinit var itemTouchHelper: ItemTouchHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.fragment_medialist, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    recycler_view.layoutManager = LinearLayoutManager(context!!)
    val adapter = MediaListAdapter(object : MediaListAdapter.MediaListCallbacks {
      override fun onItemClicked(holder: MediaListAdapter.MediaItemViewHolder) {
        activityInterface.onItemClicked(holder)
      }
    })

    recycler_view.adapter = adapter

    itemTouchHelper =
      ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START) {
        override fun onMove(
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          target: RecyclerView.ViewHolder
        ): Boolean {
          log.error("onMove()")
          return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
          log.warn("onSwiped() direction $direction")
          val item = (viewHolder as MediaListAdapter.MediaItemViewHolder).item!!
          model.removeItem(item)?.also { undo ->
            Snackbar.make(view, "${item.title} removed", Snackbar.LENGTH_LONG)
              .setAction(R.string.lbl_undo) {
                undo.invoke()
              }.show()
          }
        }

        /*  override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
          ) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
              viewHolder.itemView.alpha = 1.0f - Math.abs(dX) / viewHolder.itemView.width
              viewHolder.itemView.translationX = dX
            } else {
              super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
          }*/

      })



    model.item.observe(viewLifecycleOwner) {
      activity?.title = it.title
      if (it.isEditable) {
        itemTouchHelper.attachToRecyclerView(recycler_view)
      }
    }

    model.children.observe(viewLifecycleOwner) {
      if (model.mediaID == URI_CONTENT_PLAYBACK_QUEUE && it.isEmpty()) {
        findNavController().popBackStack()
      } else {
        adapter.submitList(it)
      }
    }


  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_medialist, menu)

    val inPlaybackQueue = model.mediaID == URI_CONTENT_PLAYBACK_QUEUE
    val showPlaybackQueue = player.playbackQueue.value?.queue?.isNotEmpty() == true
        && !inPlaybackQueue

    menu.findItem(R.id.action_queue)?.also {
      it.isVisible = showPlaybackQueue
    }

    menu.findItem(R.id.action_empty_queue)?.also {
      it.isVisible = inPlaybackQueue
    }
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_queue -> {
        findNavController().navigate(MediaListFragmentDirections.actionGlobalNavigatePlaylist())
        true
      }
      R.id.action_empty_queue -> {
        player.emptyQueue()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}

private val log = org.slf4j.LoggerFactory.getLogger(MediaListFragment::class.java)

