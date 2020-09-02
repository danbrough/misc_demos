package danbroid.exoservice

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(org.mockito.junit.MockitoJUnitRunner::class)
class Test1 {
  val items = listOf("red", "yellow", "pink")

  @Mock
  private lateinit var context: Context

  @Test
  fun test1() {
    log.info("test1")


    var count = 0
    fun register(callback: (String) -> Unit) {
      callback.invoke("OK:${count++}")

    }

    val c1 = channelFlow {
      log.trace("c1")
      send("Test1")
      send("TEst2")
      withContext(Dispatchers.IO) {
        send("Test3")
      }
      register {
        offer(it)
      }
      register {
        offer(it)
      }
    }.flowOn(Dispatchers.Unconfined)



    val c2 = flow {
      log.trace("c2")
      emit("A")
      emit("B")


    }


    runBlocking {

      c1.collect {
        log.warn("collected: $it")
      }
      c2.collect {
        log.info("C2 :$it")
      }
    }
  }


}

fun main(args: Array<String>) {

}


private val log = org.slf4j.LoggerFactory.getLogger(Test1::class.java)

