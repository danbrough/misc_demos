pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}


plugins {
  id("de.fayard.refreshVersions") version "0.40.1"
}


rootProject.name = "NextCloud Android"
include(":app")
