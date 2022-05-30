import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  //kotlin("plugin.serialization")
  //id("com.android.library")
}

group = ProjectProperties.GROUP_ID
version = ProjectProperties.VERSION_NAME


kotlin {
  linuxX64(ProjectProperties.PLATFORM_LINUX_AMD64)

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