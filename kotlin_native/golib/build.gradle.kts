import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  //kotlin("plugin.serialization")
  //id("com.android.library")
}

group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME


kotlin {
  linuxX64(ProjectVersions.PLATFORM_LINUX_AMD64)

  sourceSets {

    val nativeMain by creating {

    }

    targets.withType(KotlinNativeTarget::class).all {

      val main by compilations.getting {
        defaultSourceSet {
          dependsOn(nativeMain)
        }
      }

      binaries {
        executable("demo") { }
      }
    }

  }

}


fun buildGoLib(platform: String) = tasks.register<Exec>("golib${platform.capitalize()}") {
  //environment("ANDROID_NDK_ROOT", android.ndkDirectory.absolutePath)
  environment("PLATFORM", platform)
  doLast {
    logger.warn("golib build finished for $platform")
  }

  val scriptFile = rootProject.file("scripts/buildgo.sh")
  val outputDir = project.buildDir.resolve("lib/$platform")
  val outputFiles = listOf("libgodemo.so","libgodemo.h").map{ outputDir.resolve(it)}

  commandLine(scriptFile)

  inputs.files(project.fileTree("src/go") {
    include("**/*.go")
    include("**/*.c")
    include("**/*.h")
  } + scriptFile)

  outputs.files(outputFiles)

  doLast {
    logger.info("finished building golib for $platform")
  }

}

buildGoLib(ProjectVersions.PLATFORM_LINUX_AMD64)