import build.environment
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  //kotlin("plugin.serialization")
  //id("com.android.library")
}


group = ProjectVersions.GROUP_ID
version = ProjectVersions.VERSION_NAME
//project.logging.captureStandardOutput(org.gradle.api.logging.LogLevel.INFO)

kotlin {
  linuxX64(build.linuxAmd64.name.toString())


  sourceSets {

    val nativeMain by creating

    targets.withType(KotlinNativeTarget::class).all {

      println("TARGET: ${this.konanTarget.family}")

      val main by compilations.getting {
        cinterops.create("libgodemo") {
          packageName("stuff")
          defFile = project.file("src/interop/godemo.def")
          extraOpts(
            "-verbose",
            "-libraryPath",
            project.buildDir.resolve("lib/linuxAmd64"),
            "-compiler-option",
            "-I${project.buildDir.resolve("lib/linuxAmd64")}"
          )
        }
        defaultSourceSet {
          dependsOn(nativeMain)
        }
      }

      binaries {
        executable("demo") {
          if (konanTarget.family == org.jetbrains.kotlin.konan.target.Family.ANDROID) {
            binaryOptions["androidProgramType"] = "nativeActivity"
          }
        }
      }
    }
  }
}


fun buildGoDemoLib(platform: build.PlatformNative) =
  tasks.register<Exec>("golib${platform.name.toString().capitalize()}") {
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
      build.goBinary,
      "build", "-v",//"-x",
      "-ldflags", "-linkmode 'external'",
      "-buildmode=c-shared",
      "-o", outputFiles[0],
      "."
    )

//CGO_ENABLED=1 go build  -tags=shell,node \
//-ldflags '-linkmode external -extldflags "-static"'  -buildmode=c-archive -o $LIBFILE

    commandLine(command)


    doLast {
      val out = project.serviceOf<StyledTextOutputFactory>().create("an-output")
      if (didWork)
        out.style(StyledTextOutput.Style.Success).println("Finished building golib for $platform")
    }

  }

buildGoDemoLib(build.linuxAmd64)


tasks.register("styleTest") {
  doLast {
    val out = project.serviceOf<StyledTextOutputFactory>().create("testOutput")
    StyledTextOutput.Style.values().forEach {
      out.style(it).println("This line has the style $it")
      out.style(it)
    }
  }
}