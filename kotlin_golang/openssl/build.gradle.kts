import Common_gradle.Common.createTarget
import org.gradle.configurationcache.extensions.capitalized

plugins {
  kotlin("multiplatform")
  id("common")
}


group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME

val opensslSrcDir = rootProject.file("extras/openssl")


val opensslCheckout = tasks.register("opensslCheckout", Exec::class) {
  outputs.files(opensslSrcDir)

}
val opensslCleanSrc = tasks.register("opensslCleanSrc", Exec::class) {
  workingDir(opensslSrcDir)
  inputs.files(opensslSrcDir)
  commandLine(BuildEnvironment.gitBinary, "clean", "-xdf")
}

val opensslResetSrc = tasks.register("opensslResetSrc", Exec::class) {
  dependsOn(opensslCleanSrc)
  workingDir(opensslSrcDir)
  commandLine(BuildEnvironment.gitBinary, "reset", "--hard")
}

fun opensslConfigureSrc(platform: PlatformNative<*>) =
  tasks.register("openssl${platform.name.toString().capitalized()}Configure", Exec::class) {
    dependsOn(opensslResetSrc)
    workingDir(opensslSrcDir)
    environment(BuildEnvironment.environment(platform))

  }

fun opensslBuildTask(platform: PlatformNative<*>) =
  tasks.register("openssl${platform.name.toString().capitalized()}Build", Exec::class) {
    dependsOn(opensslResetSrc)
    group = BasePlugin.BUILD_GROUP
    commandLine("/usr/bin/date")
  }


kotlin {


  BuildEnvironment.nativeTargets.forEach { platform ->

    val buildTask = opensslBuildTask(platform)

    createTarget(platform) {

    }
  }


}
