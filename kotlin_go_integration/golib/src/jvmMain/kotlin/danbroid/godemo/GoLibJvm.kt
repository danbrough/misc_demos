package danbroid.godemo

object GoLibJvm : GoLib {

  override fun getTime(): String = JNI.getTime()

}


actual fun initGoLib(): GoLib = GoLibJvm



