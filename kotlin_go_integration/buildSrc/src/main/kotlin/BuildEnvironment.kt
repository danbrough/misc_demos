import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithHostTests
import java.io.File


enum class GOOS {
  linux, windows, android
}

enum class GOARCH(val altName: String? = null) {
  x86("386"), amd64, arm, arm64;

  override fun toString() = altName ?: name
}


val goBinary: String
  get() = ProjectProperties.getProperty("go.binary", "/usr/bin/go")
val buildCacheDir: File
  get() = File(ProjectProperties.getProperty("build.cache"))
val konanDir: File
  get() = File(ProjectProperties.getProperty("konan.dir", "${System.getProperty("user.home")}/.konan"))
val androidNdkDir: File
  get() = File(ProjectProperties.getProperty("android.ndk.dir"))
val androidNdkApiVersion: Int
  get() = ProjectProperties.getProperty("android.ndk.api.version", "23").toInt()
val buildPath: List<String>
  get() = ProjectProperties.getProperty("build.path").split("[\\s]+".toRegex())

val androidToolchainDir by lazy {
  androidNdkDir.resolve("toolchains/llvm/prebuilt/linux-x86_64").also {
    assert(it.exists()) {
      "Failed to locate ${it.absolutePath}"
    }
  }
}

val clangBinDir by lazy {
  File("$konanDir/dependencies/llvm-11.1.0-linux-x64-essentials/bin").also {
    assert(it.exists()) {
      "Failed to locate ${it.absolutePath}"
    }
  }
}

sealed class Platform(
  val name: PlatformName,
) {
  enum class PlatformName {
    android, androidNativeArm32, androidNativeArm64, androidNativeX64, androidNativeX86, iosArm32, iosArm64, iosSimulatorArm64, iosX64, js, jsBoth, jsIr, jvm, jvmWithJava, linuxArm32Hfp, linuxArm64, linuxMips32, linuxMipsel32, linuxX64, macosArm64, macosX64, mingwX64, mingwX86, tvosArm64, tvosSimulatorArm64, tvosX64, wasm, wasm32, watchosArm32, watchosArm64, watchosSimulatorArm64, watchosX64, watchosX86
  }


  override fun toString() = name.toString()
}

open class PlatformNative<T : KotlinNativeTarget>(
  name: PlatformName,
  val host: String,
  val goOS: GOOS,
  val goArch: GOARCH,
  val goArm: Int = 7
) : Platform(name) {
  val goCacheDir: File = buildCacheDir.resolve("go")
}


object LinuxX64 : PlatformNative<KotlinNativeTargetWithHostTests>(
  PlatformName.linuxX64,
  "x86_64-unknown-linux-gnu",
  GOOS.linux,
  GOARCH.amd64
)

object linuxArm64 : PlatformNative<KotlinNativeTarget>(
  PlatformName.linuxArm64,
  "aarch64-unknown-linux-gnu",
  GOOS.linux,
  GOARCH.arm64
)

object linuxArm :
  PlatformNative<KotlinNativeTarget>(PlatformName.linuxArm32Hfp, "arm-unknown-linux-gnueabihf", GOOS.linux, GOARCH.arm)

object windowsAmd64 : PlatformNative<KotlinNativeTargetWithHostTests>(
  PlatformName.mingwX64,
  "x86_64-w64-mingw32",
  GOOS.windows,
  GOARCH.amd64
)

open class PlatformAndroid<T : KotlinNativeTarget>(
  name: PlatformName,
  host: String,
  goOS: GOOS,
  goArch: GOARCH,
  goArm: Int = 7,
  val androidLibDir: String
) : PlatformNative<T>(name, host, goOS, goArch, goArm)

object androidArm :
  PlatformAndroid<KotlinNativeTarget>(
    PlatformName.androidNativeArm32,
    "armv7a-linux-androideabi",
    GOOS.android,
    GOARCH.arm,
    androidLibDir = "armeabi-v7a"
  )

object androidArm64 :
  PlatformAndroid<KotlinNativeTarget>(
    PlatformName.androidNativeArm64,
    "aarch64-linux-android",
    GOOS.android,
    GOARCH.arm64,
    androidLibDir = "arm64-v8a",
  )

object android386 :
  PlatformAndroid<KotlinNativeTarget>(
    PlatformName.androidNativeX86,
    "i686-linux-android",
    GOOS.android,
    GOARCH.x86,
    androidLibDir = "x86",
  )

object androidAmd64 : PlatformAndroid<KotlinNativeTarget>(
  PlatformName.androidNativeX64,
  "x86_64-linux-android",
  GOOS.android,
  GOARCH.amd64,
  androidLibDir = "x86_64",
)


fun PlatformNative<*>.environment(): Map<String, Any> = mutableMapOf(
  "CGO_ENABLED" to 1,
  "GOOS" to goOS,
  "GOARM" to goArm,
  "GOARCH" to goArch,
  "GOBIN" to goCacheDir.resolve("$name/bin"),
  "GOCACHE" to goCacheDir.resolve("$name/gobuild"),
  "GOCACHEDIR" to goCacheDir,
  "GOMODCACHE" to goCacheDir.resolve("mod"),
  "GOPATH" to goCacheDir.resolve(name.toString()),
  "KONAN_DATA_DIR" to goCacheDir.resolve("konan"),
  "CGO_CFLAGS" to "-O3",
).apply {

  val path = buildPath.toMutableList()



  when (this@environment) {

    linuxArm -> {
      val clangArgs = "--target=$host " +
          "--gcc-toolchain=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2 " +
          "--sysroot=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/arm-unknown-linux-gnueabihf/sysroot "
      this["CC"] = "$clangBinDir/clang $clangArgs"
      this["CXX"] = "$clangBinDir/clang++ $clangArgs"
    }

    linuxArm64 -> {
      val clangArgs = "--target=$host " +
          "--gcc-toolchain=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 " +
          "--sysroot=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot"
      this["CC"] = "$clangBinDir/clang $clangArgs"
      this["CXX"] = "$clangBinDir/clang++ $clangArgs"
    }

    LinuxX64 -> {
      val clangArgs = "--target=$host " +
          "--gcc-toolchain=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2 " +
          "--sysroot=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot"
      this["CC"] = "$clangBinDir/clang $clangArgs"
      this["CXX"] = "$clangBinDir/clang++ $clangArgs"
    }

    windowsAmd64 -> {
    }

    androidArm, android386, androidArm64, androidAmd64 -> {
      path.add(0, androidToolchainDir.resolve("bin").absolutePath)
      this["CC"] = "${host}${androidNdkApiVersion}-clang"
      this["CXX"] = "${host}${androidNdkApiVersion}-clang++"
      this["AR"] = "llvm-ar"
      this["RANLIB"] = "llvm-ranlib"
    }
  }

  this["PATH"] = path.joinToString(File.pathSeparator)
}


