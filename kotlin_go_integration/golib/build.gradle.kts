import Common_gradle.GoLib.RegisterGreeting
import Common_gradle.GoLib.registerGoLibBuild
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import  org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  id("common")


  //kotlin("plugin.serialization")
  //id("com.android.library")
}


group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME


//project.logging.captureStandardOutput(org.gradle.api.logging.LogLevel.INFO)
//apply("ProjectPlugin.gradle.kts")

/*apply {
  plugin(KotlinMultiplatformPlugin::class)
  from("common.gradle.kts")
}*/
/*
apply {
  plugin(Kotlin)
  from("common.gradle.kts")
}
*/

kotlin {
  val nativeMain by sourceSets.creating

  val goDir = project.file("src/go")

  listOf(LinuxX64).forEach {
    val golibBuildTask = registerGoLibBuild(it, goDir).get()

    val presetName = it.name.toString()
    targetFromPreset(presets[presetName], presetName) {
      if (this is KotlinNativeTarget) {
        //println("TARGET: ${this.konanTarget.family} PRESET_NAME: $presetName")

        val goLibDir = golibBuildTask.outputs.files.first().parentFile
        println("GO LIB DIR: $goLibDir")

        compilations["main"].apply {

          cinterops.create("libgodemo") {

            tasks.getAt(interopProcessingTaskName).apply {
              inputs.files(golibBuildTask.outputs)
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


RegisterGreeting("harry", "Hello Harry")

tasks.register<Common_gradle.GreetingTask>("greeting") {
  greeting.set("DUDE!")
}



