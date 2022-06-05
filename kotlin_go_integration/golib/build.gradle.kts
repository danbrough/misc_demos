import Common_gradle.Common.createTarget
import Common_gradle.Common.hostPlatform
import Common_gradle.GoLib.libsDir
import Common_gradle.GoLib.registerGoLibBuild
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeHostTest
import org.jetbrains.kotlin.gradle.tasks.KotlinTest
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  id("common")
}


group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME



kotlin {

  sourceSets {
    val commonMain by getting

    commonTest {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val nativeMain by creating {
      dependsOn(commonMain)
    }
  }


  val goDir = project.file("src/go")

  BuildEnvironment.nativeTargets.forEach { platform ->

    createTarget(platform) {

      val goLibDir = libsDir(platform)
      val golibBuildTask = registerGoLibBuild(platform, goDir, goLibDir).get()

      println("TARGET: ${this.konanTarget.family} PRESET_NAME: $name")


      compilations["main"].apply {

        cinterops.create("libgodemo") {
          packageName("golibdemo")
          defFile = project.file("src/interop/godemo.def")
          includeDirs(goLibDir, project.file("src/include"))
          tasks.getAt(interopProcessingTaskName).apply {
            inputs.files(golibBuildTask.outputs)
            dependsOn(golibBuildTask.name)
          }
        }

        if (platform.goOS != GoOS.android) {
          cinterops.create("jni") {
            packageName("platform.android")
            defFile = project.file("src/interop/jni.def")
            includeDirs(project.file("src/include"))
            when (platform.goOS) {
              GoOS.linux -> {
                includeDirs(project.file("src/include/linux"))
              }
              GoOS.windows -> {
                includeDirs(project.file("src/include/win32"))
              }
              else -> {
                TODO("add other jni headers")
              }
            }
          }
        }

        defaultSourceSet {
          dependsOn(sourceSets["nativeMain"])
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


  jvm()
}

tasks.withType(KotlinNativeHostTest::class).all {
  println("KOTLINT TEST $this type: ${this.javaClass}")
  environment("LD_LIBRARY_PATH", libsDir(hostPlatform))

}

