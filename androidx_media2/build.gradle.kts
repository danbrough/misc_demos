buildscript {


    dependencies {
    classpath("com.android.tools.build:gradle:4.2.0-alpha09")
    //classpath("com.android.tools.build:gradle:4.1.0-rc02")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    classpath(AndroidX.navigation.safeArgsGradlePlugin)


  }

  repositories {
    google()
    mavenCentral()
    mavenLocal()

    // gradlePluginPortal()
    jcenter()
/*    maven {
      setUrl("https://dl.bintray.com/kotlin/kotlin-eap")
    }*/

    // maven { setUrl( "https://dl.bintray.com/kotlin/kotlin-eap" )}

  }

}


apply("project.gradle.kts")

allprojects {

  repositories {
    google()
    jcenter()
    maven { setUrl( "https://dl.bintray.com/kotlin/kotlin-eap" )}
/*    maven {
      setUrl("https://maven.google.com/")
    }*/
    mavenCentral()
    mavenLocal()

    maven {
      setUrl("https://jitpack.io")
    }


    /*   maven {
         setUrl("https://dl.bintray.com/kotlin/kotlin-eap")
       }*/
  }
  /*configurations.all {
    if (name.toLowerCase().contains("test")) {
      resolutionStrategy.dependencySubstitution {
        substitute(module(Libs.slf4j)).with(module(Libs.logback_classic))
      }
    }
  }*/
}

/*
tasks.register("projectVersion") {
  this.description = "Prints Project.getVersionName()"
  doLast {
    println(ProjectVersions.getVersionName())
  }
}

tasks.register("nextProjectVersion") {
  this.description = "Prints Project.getIncrementedVersionName()"
  doLast {
    println(ProjectVersions.getIncrementedVersionName())
  }
}
*/

