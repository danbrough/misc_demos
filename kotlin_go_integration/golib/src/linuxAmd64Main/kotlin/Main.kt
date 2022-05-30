import kotlinx.cinterop.toKString
import platform.linux.free

fun main() {
  println("Hello World!")


  //println("The time is: ${stuff.GetTime()

  val time = stuff.GetTime()?.let {
    val s = it.toKString()
    free(it)
    s
  }

  println("The time is $time")

}