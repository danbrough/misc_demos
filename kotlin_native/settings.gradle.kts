pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
  }
}



plugins {
  id("de.fayard.refreshVersions") version "0.40.1"
}


rootProject.name = "kotlin_native_demo"

//include(":lib")
include(":golib")

/*include(":api")
include(":golib")*/
//include(":demos:ktor")
