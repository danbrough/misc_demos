import Common_gradle.GoLib.registerGoLibBuild
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

  listOf(LinuxX64,LinuxArm64).forEach {
    val golibBuildTask = registerGoLibBuild(it, goDir).get()

    val presetName = it.name.toString()
    targetFromPreset(presets[presetName], presetName) {
      if (this is KotlinNativeTarget) {
        //println("TARGET: ${this.konanTarget.family} PRESET_NAME: $presetName")

        val goLibDir = golibBuildTask.outputs.files.first().parentFile

        compilations["main"].apply {

          cinterops.create("libgodemo") {

            tasks.getAt(interopProcessingTaskName).apply {
              inputs.files(golibBuildTask.outputs)
              dependsOn(golibBuildTask.name)
            }

            packageName("golibdemo")

            defFile = project.file("src/interop/godemo.def")
            extraOpts(
              "-verbose",
              "-compiler-option",
              "-I$goLibDir",
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


            runTask?.environment("LD_LIBRARY_PATH", goLibDir)
          }
        }
      }
    }
  }
}


