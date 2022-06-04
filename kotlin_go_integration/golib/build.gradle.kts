import Common_gradle.Common.createTarget
import Common_gradle.GoLib.registerGoLibBuild
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  id("common")
}


group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME



kotlin {
  val nativeMain by sourceSets.creating


  val goDir = project.file("src/go")

  BuildEnvironment.nativeTargets.forEach { platform ->

    createTarget(platform) {


      val golibBuildTask = registerGoLibBuild(platform, goDir).get()


      println("TARGET: ${this.konanTarget.family} PRESET_NAME: $name")

      val goLibDir = golibBuildTask.outputs.files.first().parentFile
      compilations["test"].apply {
        dependencies {
          implementation(kotlin("test"))
        }

      }
      compilations["main"].apply {

        cinterops.create("libgodemo") {

          tasks.getAt(interopProcessingTaskName).apply {
            inputs.files(golibBuildTask.outputs)
            dependsOn(golibBuildTask.name)
          }

          packageName("golibdemo")
          defFile = project.file("src/interop/godemo.def")
          includeDirs("$goLibDir")
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

          runTask?.environment("LD_LIBRARY_PATH", goLibDir)
        }
      }
    }
  }

  jvm {
    compilations["test"].apply {
      dependencies {
        implementation(kotlin("test"))
      }

    }
  }
}
