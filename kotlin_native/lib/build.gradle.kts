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