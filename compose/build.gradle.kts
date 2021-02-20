// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:_")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")

    // NOTE: Do not place your application dependencies here; they belong
    // in the individual module build.gradle files
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


