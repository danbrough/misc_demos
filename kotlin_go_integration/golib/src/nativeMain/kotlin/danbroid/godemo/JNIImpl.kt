package danbroid.godemo

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import platform.android.JNIEnvVar
import platform.android.jclass
import platform.android.jstring

object JNIImpl {
  /*
JNIEXPORT jstring JNICALL Java_danbroid_godemo_JNI_getTime
  (JNIEnv *, jclass);
   */


}

@CName("Java_danbroid_godemo_JNI_getTime")
fun getTime(env: CPointer<JNIEnvVar>, thiz: jclass): jstring {
  memScoped {
    init()



    return env.pointed.pointed!!.NewStringUTF!!.invoke(
      env, godemo.GetTime()


      // KGetMessage()!!.getPointer(this)
      //"The time is ${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())}!".cstr.ptr
    )!!
  }
}

private fun init() {
  initRuntimeIfNeeded()
  Platform.isMemoryLeakCheckerActive = true
}
