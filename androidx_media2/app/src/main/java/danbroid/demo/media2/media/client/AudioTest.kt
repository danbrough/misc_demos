package danbroid.demo.media2.media.client

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getMainExecutor
import androidx.core.net.toUri
import androidx.media2.common.MediaItem
import androidx.media2.common.MediaMetadata
import androidx.media2.common.SessionPlayer
import androidx.media2.common.UriMediaItem
import androidx.media2.player.MediaPlayer
import androidx.media2.session.MediaController
import androidx.media2.session.MediaSession
import danbroid.demo.media2.media.buffState
import danbroid.demo.media2.media.playerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors


class AudioTest(val context: Context) {
  val executor = Executors.newSingleThreadExecutor()


  val player = MediaPlayer(context).also {
    it.registerPlayerCallback(
      executor,
      object : SessionPlayer.PlayerCallback() {
/*
        override fun onTracksChanged(
          player: SessionPlayer,
          tracks: MutableList<SessionPlayer.TrackInfo>
        ) {
          log.info("onTracksChanged() $tracks")
        }
*/

        override fun onPlayerStateChanged(
          player: SessionPlayer,
          @SessionPlayer.PlayerState playerState: Int
        ) {
          log.trace("onPLayerStateChanged() ${playerState.playerState}")
        }
      })
  }

  suspend fun run() {
    log.debug("run()")

    player.registerPlayerCallback(executor,
      object : SessionPlayer.PlayerCallback() {
        override fun onBufferingStateChanged(
          player: SessionPlayer,
          item: MediaItem?,
          buffState: Int
        ) {
          log.debug("onBufferingStateChanged() ${buffState.buffState}")
        }

        override fun onPlayerStateChanged(player: SessionPlayer, playerState: Int) {
          log.debug("onPlayerStateChanged() ${playerState.playerState}")
        }
      })


    withContext(Dispatchers.Main) {


      //val id = "http://192.168.1.2/music/test.mp3"
      val id = "https://h1.danbrough.org/guitar/improv/improv1.mp3"


      player.setMediaItem(
        UriMediaItem.Builder(id.toUri())
          .setStartPosition(0L)
          .setEndPosition(-1)
          .setMetadata(
            MediaMetadata.Builder()
              .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, id)
              .putString(MediaMetadata.METADATA_KEY_MEDIA_URI, id)
              .putLong(MediaMetadata.METADATA_KEY_PLAYABLE, 1)
              .putString(MediaMetadata.METADATA_KEY_TITLE, "Test Mp3")
              .build()
          )
          .build()
      )

        .also {


          it.addListener({
            val res = it.get()
            log.warn("added item: ${res.resultCode} item: ${res.mediaItem}")


            /*
                  log.debug("calling prepare..")
        player.prepare().addListener({
          log.info("prepared:")
        }, executor)
             */
            log.debug("calling play ..")
            player.play().addListener({
              log.debug("play started")
            }, executor)


          }, executor)
        }


    }

  }

}


private val log = org.slf4j.LoggerFactory.getLogger(AudioTest::class.java)
