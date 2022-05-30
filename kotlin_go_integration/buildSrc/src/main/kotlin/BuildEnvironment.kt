import BuildEnvironment.androidNdkApiVersion
import BuildEnvironment.androidToolchainDir
import BuildEnvironment.buildPath
import BuildEnvironment.clangBinDir
import BuildEnvironment.konanDir
import java.io.File


/*
PRESET: android
PRESET: androidNativeArm32
PRESET: androidNativeArm64
PRESET: androidNativeX64
PRESET: androidNativeX86
PRESET: iosArm32
PRESET: iosArm64
PRESET: iosSimulatorArm64
PRESET: iosX64
PRESET: js
PRESET: jsBoth
PRESET: jsIr
PRESET: jvm
PRESET: jvmWithJava
PRESET: linuxArm32Hfp
PRESET: linuxArm64
PRESET: linuxMips32
PRESET: linuxMipsel32
PRESET: linuxX64
PRESET: macosArm64
PRESET: macosX64
PRESET: mingwX64
PRESET: mingwX86
PRESET: tvosArm64
PRESET: tvosSimulatorArm64
PRESET: tvosX64
PRESET: wasm
PRESET: wasm32
PRESET: watchosArm32
PRESET: watchosArm64
PRESET: watchosSimulatorArm64
PRESET: watchosX64
PRESET: watchosX86
*/



enum class GOOS {
  linux, windows, android
}

enum class GOARCH(val altName: String? = null) {
  x86("386"), amd64, arm, arm64;

  override fun toString() = altName ?: name
}


object BuildEnvironment {


  lateinit var goBinary: String
  lateinit var buildCacheDir: File
  lateinit var konanDir: File
  lateinit var androidNdkDir: File
  var androidNdkApiVersion: Int = 23
  lateinit var buildPath: List<String>

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

  fun configure() {
    goBinary = ProjectVersions.getProperty("go.binary", "/usr/bin/go")
    buildCacheDir = File(ProjectVersions.getProperty("build.cache"))
    konanDir = File(ProjectVersions.getProperty("konan.dir", "${System.getProperty("user.home")}/.konan"))
    androidNdkDir = File(ProjectVersions.getProperty("android.ndk.dir"))
    androidNdkApiVersion = ProjectVersions.getProperty("android.ndk.api.version").toInt()
    buildPath = ProjectVersions.getProperty("build.path").split("[\\s]+".toRegex())
  }

}

enum class Platform(
  val host: String,
  val goOS: GOOS,
  val goArch: GOARCH,
  val goArm: Int = 7,
  val androidLibDir: String? = null
) {

  linuxAmd64("x86_64-unknown-linux-gnu", GOOS.linux, GOARCH.amd64),
  linuxArm64("aarch64-unknown-linux-gnu", GOOS.linux, GOARCH.arm64),
  linuxArm("arm-unknown-linux-gnueabihf", GOOS.linux, GOARCH.arm),
  windowsAmd64("x86_64-w64-mingw32", GOOS.windows, GOARCH.amd64),
  androidArm("armv7a-linux-androideabi", GOOS.android, GOARCH.arm, androidLibDir = "armeabi-v7a"),
  androidArm64("aarch64-linux-android", GOOS.android, GOARCH.arm64, goArm = 7, androidLibDir = "arm64-v8a"),
  android386("i686-linux-android", GOOS.android, GOARCH.x86, androidLibDir = "x86"),
  androidAmd64("x86_64-linux-android", GOOS.android, GOARCH.amd64, androidLibDir = "x86_64");

  val goCacheDir: File = BuildEnvironment.buildCacheDir.resolve("go")
  val isAndroid: Boolean = androidLibDir != null


}

fun Platform.environment(): Map<String, Any> = mutableMapOf(
  "CGO_ENABLED" to 1,
  "GOOS" to goOS,
  "GOARM" to goArm,
  "GOARCH" to goArch,
  "GOBIN" to goCacheDir.resolve("$name/bin"),
  "GOCACHE" to goCacheDir.resolve("$name/gobuild"),
  "GOCACHEDIR" to goCacheDir,
  "GOMODCACHE" to goCacheDir.resolve("mod"),
  "GOPATH" to goCacheDir.resolve(name),
  "KONAN_DATA_DIR" to goCacheDir.resolve("konan"),
  "CGO_CFLAGS" to "-O3",
).apply {


  val path = buildPath.toMutableList()

  when (this@environment) {
    Platform.linuxArm -> {
      val clangArgs = "--target=$host " +
          "--gcc-toolchain=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2 " +
          "--sysroot=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/arm-unknown-linux-gnueabihf/sysroot "
      this["CC"] = "$clangBinDir/clang $clangArgs"
      this["CXX"] = "$clangBinDir/clang++ $clangArgs"
    }

    Platform.linuxArm64 -> {
      val clangArgs = "--target=$host " +
          "--gcc-toolchain=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 " +
          "--sysroot=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot"
      this["CC"] = "$clangBinDir/clang $clangArgs"
      this["CXX"] = "$clangBinDir/clang++ $clangArgs"
    }

    Platform.linuxAmd64 -> {
      //        this["CC"] = "gcc"
//          this["CXX"] = "g++"

      val clangArgs = "--target=$host " +
          "--gcc-toolchain=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2 " +
          "--sysroot=$konanDir/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/x86_64-unknown-linux-gnu/sysroot"
      this["CC"] = "$clangBinDir/clang $clangArgs"
      this["CXX"] = "$clangBinDir/clang++ $clangArgs"
    }
    Platform.windowsAmd64 -> {

    }

    Platform.androidArm, Platform.android386, Platform.androidArm64, Platform.androidAmd64 -> {
      path.add(0, androidToolchainDir.resolve("bin").absolutePath)
      this["CC"] = "${host}${androidNdkApiVersion}-clang"
      this["CXX"] = "${host}${androidNdkApiVersion}-clang++"
      this["AR"] = "llvm-ar"
      this["RANLIB"] = "llvm-ranlib"
    }
  }

  this["PATH"] = path.joinToString(File.pathSeparator)
}