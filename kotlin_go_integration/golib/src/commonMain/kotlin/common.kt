
interface GoLib {

  fun getTime(): String
}

expect fun initGoLib(): GoLib


fun main() {
  println("running golibdemo ..")

  val golib = initGoLib()


  println(golib.getTime())

}
