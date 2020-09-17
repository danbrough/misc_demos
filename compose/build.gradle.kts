buildscript {

  dependencies {
    classpath("com.android.tools.build:gradle:4.2.0-alpha10")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    classpath(AndroidX.navigation.safeArgsGradlePlugin)
  }

  repositories {
    google()
    jcenter()
  }

}

apply("project.gradle.kts")

allprojects {

  repositories {
    google()
    jcenter()
    maven("https://jitpack.io")
  }
}

