import Common_gradle.Common.createTarget
import Common_gradle.GoLib.libsDir
import Common_gradle.GoLib.registerGoLibBuild
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeHostTest
import org.jetbrains.kotlin.konan.target.Family

plugins {
  kotlin("multiplatform")
  id("common")
}


group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME



kotlin {
  jvm {

  }

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

    val jvmMain by getting {
      dependsOn(commonMain)
    }
  }


  val goDir = project.file("src/go")

  BuildEnvironment.nativeTargets.forEach { platform ->

    createTarget(platform) {

      val goLibDir = libsDir(platform)
      val golibBuildTask = registerGoLibBuild(platform, goDir, goLibDir).get()

      //println("TARGET: ${this.konanTarget.family} PRESET_NAME: $name")


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


}

tasks.withType(KotlinNativeHostTest::class).all {
  //println("KOTLINT TEST $this type: ${this.javaClass}")
  environment("LD_LIBRARY_PATH", libsDir(BuildEnvironment.hostPlatform))

}

tasks.register("jniHeaders", Exec::class) {
  kotlin.targets.withType(KotlinJvmTarget::class) {

    val classpath = compilations["main"].compileKotlinTask.outputs.files.joinToString(File.pathSeparator)
    val commandLine = listOf(
      BuildEnvironment.javah,
      "-cp", classpath,
      "-d", project.buildDir.resolve("jni").also {
        if (!it.exists()) it.mkdirs()
      },
      "danbroid.godemo.JNI"
    )

    println("commandLine: ${commandLine.joinToString(" ")}")
    commandLine(commandLine)

  }

}

