import Common_gradle.Common.createTarget
import org.gradle.configurationcache.extensions.capitalized

plugins {
  kotlin("multiplatform")
  id("common")
}

val opensslTag = "OpenSSL_1_1_1o"

val PlatformNative<*>.opensslPlatform
  get() = when (this) {
    LinuxX64 -> "linux-x86_64"
    LinuxArm64 -> "linux-aarch64"
    LinuxArm -> "linux-armv4"
    AndroidArm -> "android-arm"
    AndroidArm64 -> "android-arm64"
    Android386 -> "android-x86"
    AndroidAmd64 -> "android-x86_64"
    MingwX64 -> "mingw64"
    else -> TODO("Add support for $this")
  }

val PlatformNative<*>.opensslPrefix
  get() = project.file("lib/$name")

group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME

val opensslSrcDir = rootProject.file("openssl/src")

//fun gitCommand(args:List<String>,)

/*fun gitCommand(vararg args: String, conf: Exec.() -> Unit = {}): TaskProvider<Exec> {
  val task by tasks.registering(Exec::class) {
    workingDir(opensslSrcDir)
    commandLine(BuildEnvironment.gitBinary + args)
    conf.invoke(this)
  }
  return task
}*/


fun TaskContainer.gitCommand(
  vararg args: String, action: Exec.() -> Unit
): RegisteringDomainObjectDelegateProviderWithTypeAndAction<TaskContainer, Exec> =
  RegisteringDomainObjectDelegateProviderWithTypeAndAction.of(this, Exec::class) {
    workingDir(opensslSrcDir)

    commandLine(listOf(BuildEnvironment.gitBinary) + args)
    action()
  }

val srcClone by tasks.gitCommand(
  "clone", "https://github.com/openssl/openssl", opensslSrcDir.absolutePath
) {
  onlyIf {
    !opensslSrcDir.exists()
  }
}


val srcClean by tasks.gitCommand("clean", "-xdf") {
  dependsOn(srcClone)
}

val srcReset by tasks.gitCommand("reset", "--hard") {
  dependsOn(srcClean)
}

val srcCheckout by tasks.gitCommand("checkout", opensslTag) {
  dependsOn(srcReset)
}

fun configureTask(platform: PlatformNative<*>) =
  tasks.register("configure${platform.name.toString().capitalized()}", Exec::class) {
    doFirst {
      println("RUNNING CONFIGURE!!! $platform")
    }
    dependsOn(srcCheckout.name)
    workingDir(opensslSrcDir)
    environment(BuildEnvironment.environment(platform))
    commandLine(
      "./Configure", platform.opensslPlatform, "no-shared", "--prefix=${platform.opensslPrefix}"
    )
  }

fun buildTask(platform: PlatformNative<*>) {
  val configureTask = configureTask(platform).get()

  tasks.register("build${platform.name.toString().capitalized()}", Exec::class) {
    doFirst {
      platform.opensslPrefix.parentFile.also {
        if (!it.exists()) it.mkdirs()
      }
    }

    platform.opensslPrefix.resolve("lib/libssl.a").exists().also {
      isEnabled = !it
      configureTask.isEnabled = !it
    }

    //dependsOn("configure${platform.name.toString().capitalized()}")
    dependsOn(configureTask)
    workingDir(opensslSrcDir)
    outputs.files(fileTree(platform.opensslPrefix) {
      include("lib/*.a", "lib/*.so", "lib/*.h")
    })
    environment(BuildEnvironment.environment(platform))
    group = BasePlugin.BUILD_GROUP
    commandLine("make", "install_sw")

  }
}


kotlin {


  listOf(
    LinuxX64,
    LinuxArm64,
    LinuxArm,
    AndroidArm,
    AndroidArm64,
    AndroidAmd64,
    Android386,
    MingwX64,
  ).forEach { platform ->


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