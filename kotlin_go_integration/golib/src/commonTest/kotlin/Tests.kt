import kotlin.test.Test

class Tests {
  @Test
  fun getTime() {
    val golib = initGoLib()
    println("The time is: ${golib.getTime()}")
  }
}