package danbroid.media.service


val testTracks = testData {
  item {
    id = "http://colostreaming.com:8094"
    title = "Radio SHE"
  }

  item {
    id = "http://curiosity.shoutca.st:9073/stream"
    title = "NZ Metal"
  }

  item {
    title = "U80s"
    id = "http://ice4.somafm.com/u80s-256-mp3"
  }

  item {
    title = "RNZ"
    id = "http://radionz-ice.streamguys.com/National_aac128"
  }

  item {
    title = "Opus Test"
    id = "https://h1.danbrough.org/guitar/improv/improv1.opus"
  }

  item {
    title = "Flac Test"
    id = "https://h1.danbrough.org/guitar/improv/improv2.flac"
  }


  item {
    title = "MP3 Test"
    id = "https://h1.danbrough.org/guitar/improv/improv3.mp3"
  }


  item {
    title = "Ogg Test"
    id = "https://h1.danbrough.org/guitar/improv/improv4.ogg"
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
