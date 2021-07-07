buildscript {


  dependencies {
    //classpath("com.android.tools.build:gradle:4.2.0-alpha09")
    classpath("com.android.tools.build:gradle:_")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")


  }

  repositories {
    //mavenLocal()
    google()
    mavenCentral()
   // jcenter()
  }

}


apply("project.gradle.kts")

subprojects {

  repositories {
    //mavenLocal()
    google()
   // jcenter()
    mavenCentral()
  //  maven("https://h1.danbrough.org/maven/")
    maven("https://jitpack.io")
  }
  /*configurations.all {
    if (name.toLowerCase().contains("test")) {
      resolutionStrategy.dependencySubstitution {
        substitute(module(Libs.slf4j)).with(module(Libs.logback_classic))
      }
    }
  }*/
}



