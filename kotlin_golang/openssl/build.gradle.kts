import Common_gradle.Common.createTarget
import org.gradle.configurationcache.extensions.capitalized

plugins {
  kotlin("multiplatform")
  id("common")
}


group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME

fun opensslBuildTask(platform: PlatformNative<*>): TaskProvider<Task> {
  val srcDir = rootProject.file("extras/openssl/src")

  val cleanTask = tasks.register("opensslCleanSrc", Exec::class) {
    workingDir(srcDir)
    commandLine("/usr/bin/git", "clean", "-xdf")
  }

  return tasks.register("openssl${platform.name.toString().capitalized()}") {
    dependsOn(cleanTask)
    group = BasePlugin.BUILD_GROUP
  }
}

kotlin {


  BuildEnvironment.nativeTargets.forEach { platform ->

    val buildTask = opensslBuildTask(platform)

    createTarget(platform) {

    }
  }


}
