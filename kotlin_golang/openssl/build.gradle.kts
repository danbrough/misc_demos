import Common_gradle.Common.createTarget
import org.gradle.configurationcache.extensions.capitalized

plugins {
  kotlin("multiplatform")
  id("common")
}

val PlatformNative<*>.opensslPlatform
  get() = when (this) {
    LinuxX64 -> "linux-x86_64"
    LinuxArm64 -> "linux-aarch64"
    LinuxArm -> "linux-armv4"
    else -> TODO("Add support for $this")
  }

val PlatformNative<*>.opensslPrefix
  get() = project.file("lib/$name")

group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME

val opensslSrcDir = rootProject.file("openssl/src")
val opensslTag = "OpenSSL_1_1_1o"


val srcClone by tasks.registering(Exec::class) {
  onlyIf {
    !opensslSrcDir.exists()
  }
  commandLine(BuildEnvironment.gitBinary, "clone", "https://github.com/openssl/openssl", opensslSrcDir)
}

val srcClean by tasks.registering(Exec::class) {
  dependsOn(srcClone)
  workingDir(opensslSrcDir)
  commandLine(BuildEnvironment.gitBinary, "clean", "-xdf")
}

val srcReset by tasks.registering(Exec::class) {
  dependsOn(srcClean)
  workingDir(opensslSrcDir)
  commandLine(BuildEnvironment.gitBinary, "reset", "--hard")
}

val srcCheckout by tasks.registering(Exec::class) {
  dependsOn(srcReset)
  workingDir(opensslSrcDir)
  commandLine(BuildEnvironment.gitBinary, "checkout", opensslTag)
}

fun configureTask(platform: PlatformNative<*>) =
  tasks.register("configure${platform.name.toString().capitalized()}", Exec::class) {
    dependsOn(srcCheckout)
    workingDir(opensslSrcDir)
    environment(BuildEnvironment.environment(platform))
    commandLine("./Configure", platform.opensslPlatform, "no-shared", "--prefix=${platform.opensslPrefix}")
  }

fun buildTask(platform: PlatformNative<*>) =
  tasks.register("build${platform.name.toString().capitalized()}", Exec::class) {
    doFirst {
      platform.opensslPrefix.parentFile.also {
        if (!it.exists()) it.mkdirs()
      }
      println("OUTPUTS: ${outputs.files.files}")

    }
    dependsOn("configure${platform.name.toString().capitalized()}")
    workingDir(opensslSrcDir)
    outputs.files(fileTree(platform.opensslPrefix) {
      include("lib/*.a", "lib/*.so", "lib/*.h")
    })
    environment(BuildEnvironment.environment(platform))
    group = BasePlugin.BUILD_GROUP
    commandLine("make", "install_sw")

  }


kotlin {


  listOf(LinuxX64, LinuxArm64, LinuxArm).forEach { platform ->

    configureTask(platform)

    buildTask(platform)

    createTarget(platform) {

    }
  }


}
/*
echo OPENSSL is $OPENSSL
CRYPTO_LIB=$OPENSSL/lib/libcrypto.a

if [ -f $CRYPTO_LIB ]; then
  echo not building openssl as $CRYPTO_LIB exists
else
  echo OPENSSL_PLATFORM $OPENSSL_PLATFORM
  echo OPENSSL $OPENSSL
  echo CC $CC CXX: $CXX
  echo CFLAGS $CFLAGS
  echo SYSROOT $SYSROOT
  echo CROSS_PREFIX $CROSS_PREFIX
  echo ANDROID_API $ANDROID_API
  sleep 2

  clean_src
  cd $SRC

  if [ "$GOOS" == "android" ]; then
    ./Configure $OPENSSL_PLATFORM no-shared -D__ANDROID_API__=$ANDROID_API --prefix="$OPENSSL" $EXTRAS || exit 1
  else
    ./Configure --prefix="$OPENSSL" $OPENSSL_PLATFORM   $EXTRAS || exit 1
  fi
  make install_sw || exit 1
fi
 */