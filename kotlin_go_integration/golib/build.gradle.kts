import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import  org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  //kotlin("plugin.serialization")
  //id("com.android.library")
}


group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME
//project.logging.captureStandardOutput(org.gradle.api.logging.LogLevel.INFO)
//apply("ProjectPlugin.gradle.kts")


kotlin {
  val nativeMain by sourceSets.creating

  listOf(LinuxX64).forEach {
    val golibBuildTask = registerGolibBuildTask(LinuxX64)

    val presetName = it.name.toString()
    targetFromPreset(presets[presetName], presetName) {
      if (this is KotlinNativeTarget) {
        println("TARGET: ${this.konanTarget.family} PRESET_NAME: $presetName")

        compilations["main"].apply {

          cinterops.create("libgodemo") {

            tasks.getAt(interopProcessingTaskName).apply {
              inputs.files(golibBuildTask.get().outputs)
              dependsOn(golibBuildTask.name)
            }

            packageName("stuff")


            /*     this.linkerOpts(
                   listOf(
                     "-L/home/dan/workspace/demos/kotlin_go_integration/golib/build/lib/linuxX64",
                     "-lgodemo",
                   )
                 )*/

            defFile = project.file("src/interop/godemo.def")
            extraOpts(
              "-verbose",
              "-compiler-option",
              "-I${project.buildDir.resolve("lib/$presetName")}",
            )
          }
          defaultSourceSet {
            dependsOn(nativeMain)
          }
        }


        binaries {
          executable("demo") {
            if (konanTarget.family == Family.ANDROID) {
              binaryOptions["androidProgramType"] = "nativeActivity"
            }
          }
        }
      }
    }
  }
}

fun registerGolibBuildTask(platform: PlatformNative<*>) =
  tasks.register<Exec>("golib${platform.name.toString().capitalize()}") {
    //environment("ANDROID_NDK_ROOT", android.ndkDirectory.absolutePath)
    environment("PLATFORM", platform)

    val outputDir = project.buildDir.resolve("lib/$platform")
    val goModule = project.file("src/go")
    assert(outputDir.mkdirs())

    val goSrcFiles = fileTree(goModule) {
      include("**/*.go")
      include("**/*.c")
      include("**/*.h")
      include("**/*.mod")
    }

    val outputFiles = listOf("libgodemo.so", "libgodemo.h").map { outputDir.resolve(it) }
    inputs.files(goSrcFiles)
    outputs.files(outputFiles)

    workingDir(goModule)

    val commandEnvironment = platform.environment()
    environment(commandEnvironment)

    group = BasePlugin.BUILD_GROUP

    val command = listOf(
      goBinary,
      "build", "-v",//"-x",
      "-trimpath",
      "-ldflags", "-linkmode 'external'",
      "-buildmode=c-shared",
      "-o", outputFiles[0],
      "."
    )

//CGO_ENABLED=1 go build  -tags=shell,node \
//-ldflags '-linkmode external -extldflags "-static"'  -buildmode=c-archive -o $LIBFILE

    commandLine(command)
    val out = project.serviceOf<StyledTextOutputFactory>().create("golibOutput")

    doFirst {
      out.style(StyledTextOutput.Style.Info).println("Building golib for $platform")
      out.style(StyledTextOutput.Style.ProgressStatus).println("environment: $commandEnvironment")
    }
    doLast {
      if (didWork)
        out.style(StyledTextOutput.Style.Success).println("Finished building golib for $platform")
    }

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

