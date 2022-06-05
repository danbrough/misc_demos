
object GoLibJvm : GoLib {
  override fun getTime(): String = "GoLibJvm:getTime() not implemented"

}

actual fun initGoLib(): GoLib = GoLibJvm

fun main() {
  println("Hello from jvm main")
}