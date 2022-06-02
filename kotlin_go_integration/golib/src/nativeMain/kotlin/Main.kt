import kotlinx.cinterop.toKString
import platform.posix.free

fun main() {
  println("running golibdemo ..")


  val time = golibdemo.GetTime()?.let {
    val s = it.toKString()
    free(it)
    s
  }

  println(time)

}