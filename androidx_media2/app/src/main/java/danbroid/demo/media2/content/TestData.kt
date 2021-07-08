package danbroid.demo.media2.content

import androidx.core.net.toUri
import androidx.media2.common.MediaItem
import androidx.media2.common.UriMediaItem
import danbroid.media.service.MediaLibrary

const val ipfs_gateway = "https://cloudflare-ipfs.com"

val testTracks = testData {

  item {

    id = "http://sohoradioculture.doughunt.co.uk:8000/320mp3"
    title = "Soho NYC"
    subtitle = "Soho radio from NYC"
    imageURI = "${ipfs_gateway}/ipns/k51qzi5uqu5dkfj7jtuefs73phqahshpa4nl7whcjntlq8v1yqi06fv6zjy3d3/media/soho_nyc.png"
  }

  item {
    id = "http://sohoradiomusic.doughunt.co.uk:8000/320mp3"
    title = "Soho UK"
    subtitle = "Soho radio from London"
    imageURI = "${ipfs_gateway}/ipns/k51qzi5uqu5dkfj7jtuefs73phqahshpa4nl7whcjntlq8v1yqi06fv6zjy3d3/media/soho_uk.png"
  }

  item {
    id = "http://colostreaming.com:8094"
    title = "Radio SHE"
    subtitle = "Some cheesy rock station from florida"
    imageURI = "${ipfs_gateway}/ipns/k51qzi5uqu5dkfj7jtuefs73phqahshpa4nl7whcjntlq8v1yqi06fv6zjy3d3/media/RadioSHE.png"

  }

  item {
    id = "http://curiosity.shoutca.st:9073/stream"
    title = "NZ Metal"
    subtitle = "Its heavy metal!!!"
    imageURI = "https://h1.danbrough.org/nzrp/metalradio.png"
  }

  item {
    title = "U80s"
    id = "http://ice4.somafm.com/u80s-256-mp3"
    subtitle = "Underground 80's SOMAFM"
    imageURI = "https://api.somafm.com/img/u80s-120.png"
  }

  item {
    title = "RNZ"
    id = "http://radionz-ice.streamguys.com/National_aac128"
    subtitle = "National Radio New Zealand"
    imageURI = "https://www.rnz.co.nz/brand-images/rnz-national.jpg"
  }

  item {
    title = "The Rock"
    id = "http://livestream.mediaworks.nz/radio_origin/rock_128kbps/playlist.m3u8"
    subtitle = "the rock"
    imageURI = "${ipfs_gateway}/ipns/audienz.danbrough.org/media/the_rock.png"
  }

  item {
    title = "Opus Test"
    id = "https://h1.danbrough.org/guitar/improv/improv1.opus"
    subtitle = "Improv1 - Dan Brough"
  }

  item {
    title = "Flac Test"
    id = "https://h1.danbrough.org/guitar/improv/improv2.flac"
    subtitle = "Improv2 - Dan Brough"
  }


  item {
    title = "MP3 Test"
    id = "https://h1.danbrough.org/guitar/improv/improv3.mp3"
    subtitle = "Improv3 - Dan Brough"
  }


  item {
    title = "Ogg Test"
    id = "https://h1.danbrough.org/guitar/improv/improv4.ogg"
    subtitle = "Improv4 - Dan Brough"
  }

  item {
    title = "Ogg Test 2"
    subtitle = "Electric Youth - Runaway"
    id = "http://192.168.1.2/music/Electric%20Youth/Innerworld/02%20-%20Runaway.ogg"
  }


  item {
    title = "Short OGA"
    id = "https://h1.danbrough.org/media/tests/test.oga"
  }
}

@DslMarker
annotation class TestDataDSL

@TestDataDSL
class TestData {
  val testData = mutableListOf<AudioTrack>()
}

@TestDataDSL
fun testData(block: TestData.() -> Unit) = TestData().apply {
  block()
}

@TestDataDSL
fun TestData.item(block: AudioTrack.() -> Unit) = AudioTrack("").also {
  it.block()
  testData.add(it)
}


class TestDataLibrary : MediaLibrary {
  override suspend fun loadItem(mediaID: String): MediaItem? =
      testTracks.testData.firstOrNull {
        it.id == mediaID
      }?.let {
        UriMediaItem.Builder(mediaID.toUri())
            .setStartPosition(0L).setEndPosition(-1L)
            .setMetadata(it.toMediaMetadata().build()).build()
      }


}

private val log = danbroid.logging.getLog(TestData::class)
