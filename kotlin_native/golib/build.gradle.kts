import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.nio.file.Paths

plugins {
  kotlin("multiplatform")
  //kotlin("plugin.serialization")
  //id("com.android.library")
}


group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME
//project.logging.captureStandardOutput(org.gradle.api.logging.LogLevel.INFO)

kotlin {
  linuxX64(ProjectVersions.PLATFORM_LINUX_AMD64)
  linuxArm32Hfp(ProjectVersions.PLATFORM_LINUX_ARM32)
  linuxArm64(ProjectVersions.PLATFORM_LINUX_ARM64)
  androidNativeArm32(ProjectVersions.PLATFORM_ANDROID_ARM)

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


fun buildGoLib(platform: BuildEnvironment.Platform) = tasks.register<Exec>("golib${platform.name.capitalize()}") {
  //environment("ANDROID_NDK_ROOT", android.ndkDirectory.absolutePath)
  environment("PLATFORM", platform)
  doLast {
    logger.warn("golib build finished for $platform")
  }

  val outputDir = project.buildDir.resolve("lib/$platform")
  val goModule = project.file("src/go")
  assert(outputDir.mkdirs())

  val goSrcFiles = fileTree(goModule) {
    include("**/*.go")
    include("**/*.c")
    include("**/*.h")
    include("**/*.mod")
  }
  val outputFiles = listOf("libgodemo.a", "libgodemo.h").map { outputDir.resolve(it) }
  inputs.files(goSrcFiles)
  outputs.files(outputFiles)

  workingDir(goModule)

  environment(platform.environment().also {
    println("environment: $it")
  })

  group = BasePlugin.BUILD_GROUP

  val command = listOf(
    BuildEnvironment.goBinary,
    "build", "-v",//"-x",
    "-ldflags", "-linkmode 'external' -extldflags='-static'",
    "-buildmode=c-archive",
    "-o", outputFiles[0]
  )

//CGO_ENABLED=1 go build  -tags=shell,node \
//-ldflags '-linkmode external -extldflags "-static"'  -buildmode=c-archive -o $LIBFILE

  commandLine(command)


  doLast {
    val out = project.serviceOf<StyledTextOutputFactory>().create("an-output")
    if (this.didWork)
      out.style(StyledTextOutput.Style.Success).println("Finished building golib for $platform")
  }

}

BuildEnvironment.platforms.forEach {
  buildGoLib(it)
}

tasks.register("styleTest") {
  doLast {
    val out = project.serviceOf<StyledTextOutputFactory>().create("testOutput")
    StyledTextOutput.Style.values().forEach {
      out.style(it).println("This line has the style $it")
      out.style(it)
    }
  }
}