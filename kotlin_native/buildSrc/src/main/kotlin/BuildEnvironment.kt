import java.io.File

object BuildEnvironment {


  enum class GOOS {
    linux, windows, android
  }

  enum class GOARCH(val altName: String? = null) {
    x86("386"), amd64, arm, arm64;

    override fun toString() = altName ?: name
  }


  val platforms by lazy { listOf(Platform.linuxArm, Platform.linuxArm64, Platform.linuxAmd64) }

  enum class Platform(val goOS: GOOS, val goArch: GOARCH, val goArm: Int = 7) {
    linuxAmd64(GOOS.linux, GOARCH.amd64),
    linuxArm64(GOOS.linux, GOARCH.arm64),
    linuxArm(GOOS.linux, GOARCH.arm),
    windowsAmd64(GOOS.windows, GOARCH.amd64),
    androidArm(GOOS.android, GOARCH.arm),
    androidArm64(GOOS.android, GOARCH.arm64),
    android386(GOOS.android, GOARCH.x86),
    androidAmd64(GOOS.android, GOARCH.amd64);

    val goCacheDir: File = buildCacheDir.resolve("go")

    fun environment(): Map<String, Any> = mutableMapOf(
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
      val clangBinDir = "$konanDir/dependencies/llvm-11.1.0-linux-x64-essentials/bin"


      when (this@Platform) {
        linuxArm -> {
          val clangArgs = "--target=arm-unknown-linux-gnueabihf " +
              "--gcc-toolchain=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2 " +
              "--sysroot=$konanDir/dependencies/arm-unknown-linux-gnueabihf-gcc-8.3.0-glibc-2.19-kernel-4.9-2/arm-unknown-linux-gnueabihf/sysroot "
          this["CC"] = "$clangBinDir/clang $clangArgs"
          this["CXX"] = "$clangBinDir/clang++ $clangArgs"
        }

        linuxArm64 -> {
          val clangArgs = "--target=aarch64-unknown-linux-gnu " +
              "--gcc-toolchain=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2 " +
              "--sysroot=$konanDir/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/aarch64-unknown-linux-gnu/sysroot"
          this["CC"] = "$clangBinDir/clang $clangArgs"
          this["CXX"] = "$clangBinDir/clang++ $clangArgs"
        }
        else -> {}
      }
    }

  }


  lateinit var goBinary: String
  lateinit var buildCacheDir: File
  lateinit var konanDir: File

  fun configure() {
    goBinary = ProjectVersions.getProperty("go.binary", "/usr/bin/go")!!
    buildCacheDir = File(ProjectVersions.getProperty("build.cache", null)!!)
    konanDir = File(ProjectVersions.getProperty("konan.dir", "${System.getProperty("user.home")}/.konan")!!)
  }

}