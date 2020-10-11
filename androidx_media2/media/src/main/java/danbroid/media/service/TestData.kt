package danbroid.media.service


val testTracks = testData {
  item {
    id = "http://colostreaming.com:8094"
    title = "Radio SHE"
    subtitle = "Some cheesy rock station from florida"
    imageURI = "http://shemiamiradio.com/graphics/WSHE-she-radio-logo.png"

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
    imageURI = "https://ipfs.io/ipfs/QmZGbcdnLgexhMjt73PfvpGXL9d2kRiJiaQDhiCbCct63Q/therock.png"
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
  val testData = mutableListOf<TrackMetadata>()
}

@TestDataDSL
fun testData(block: TestData.() -> Unit) = TestData().apply {
  block()
}

@TestDataDSL
fun TestData.item(block: TrackMetadata.() -> Unit) = TrackMetadata("").also {
  it.block()
  testData.add(it)
}

fun loadTestData(id: String) = testTracks.testData.firstOrNull { it.id == id }


private val log = org.slf4j.LoggerFactory.getLogger(TestData::class.java)
