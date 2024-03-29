import Common_gradle.Common.createTarget
import org.gradle.configurationcache.extensions.capitalized
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  id("common")
  `maven-publish`
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
  get() = project.buildDir.resolve("openssl/$name")


val PlatformNative<*>.opensslSrcDir: File
  get() = project.buildDir.resolve("openssl/src/$opensslTag/$name")

group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME

val opensslGitDir = project.file("src/openssl.git")

//fun gitCommand(args:List<String>,)

/*fun gitCommand(vararg args: String, conf: Exec.() -> Unit = {}): TaskProvider<Exec> {
  val task by tasks.registering(Exec::class) {
    workingDir(opensslSrcDir)
    commandLine(BuildEnvironment.gitBinary + args)
    conf.invoke(this)
  }
  return task
}*/


val srcClone by tasks.registering(Exec::class) {
  commandLine(
    BuildEnvironment.gitBinary,
    "clone",
    "--bare",
    "https://github.com/openssl/openssl",
    opensslGitDir
  )
  outputs.dir(opensslGitDir)
  onlyIf {
    !opensslGitDir.exists()
  }

}

fun srcPrepare(platform: PlatformNative<*>): Exec =
  tasks.create("srcPrepare${platform.name.toString().capitalized()}", Exec::class) {
    val srcDir = platform.opensslSrcDir
    dependsOn(srcClone)
    isEnabled = !srcDir.exists()

    commandLine(
      BuildEnvironment.gitBinary, "clone", "--branch", opensslTag, opensslGitDir, srcDir
    )
  }


fun configureTask(platform: PlatformNative<*>): Exec {

  val srcPrepare = srcPrepare(platform)

  return tasks.create("configure${platform.name.toString().capitalized()}", Exec::class) {
    dependsOn(srcPrepare)
    workingDir(platform.opensslSrcDir)
    environment(BuildEnvironment.environment(platform))
    val args = mutableListOf(
      "./Configure", platform.opensslPlatform,
      //"no-shared",
      "no-tests", "--prefix=${platform.opensslPrefix}"
    )
    if (platform.isAndroid) args += "-D__ANDROID_API__=${BuildEnvironment.androidNdkApiVersion} "
    else if (platform.isWindows) args += "--cross-compile-prefix=${platform.host}-"
    commandLine(args)
  }
}

fun buildTask(platform: PlatformNative<*>) {
  val configureTask = configureTask(platform)

  tasks.create("build${platform.name.toString().capitalized()}", Exec::class) {
/*
    doFirst {
      platform.opensslPrefix.parentFile.also {
        if (!it.exists()) it.mkdirs()
      }
    }
*/


    platform.opensslPrefix.resolve("lib/libssl.a").exists().also {
      isEnabled = !it
      configureTask.isEnabled = !it
      println("CONFIGURE TASK ENABLED: ${configureTask.name} ${!it}")
    }
    dependsOn(configureTask.name)

    //dependsOn("configure${platform.name.toString().capitalized()}")


    tasks.getAt("buildAll").dependsOn(this)
    workingDir(platform.opensslSrcDir)
    outputs.files(fileTree(platform.opensslPrefix) {
      include("lib/*.a", "lib/*.so", "lib/*.h")
    })
    environment(BuildEnvironment.environment(platform))
    group = BasePlugin.BUILD_GROUP
    commandLine("make", "install_sw")

  }
}


kotlin {

  val buildAll by tasks.registering

  BuildEnvironment.nativeTargets.forEach { platform ->

    createTarget(platform) {
      compilations["main"].apply {
        cinterops.create("openssl") {
          this.defFile = project.file("src/openssl.def")
        }
      }
    }

    buildTask(platform)
  }
}


publishing {
  publications {
    kotlin.targets.withType(KotlinNativeTarget::class) {

    }
  }

  repositories {
    maven(ProjectProperties.MAVEN_REPO)
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