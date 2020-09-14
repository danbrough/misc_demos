buildscript {

  dependencies {
    //classpath("com.android.tools.build:gradle:4.2.0-alpha08")
    classpath("com.android.tools.build:gradle:4.1.0-rc02")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    classpath(AndroidX.navigation.safeArgsGradlePlugin)
  }

  repositories {
    mavenLocal()

    google()
    mavenCentral()

    // gradlePluginPortal()
    jcenter()
/*    maven {
      setUrl("https://dl.bintray.com/kotlin/kotlin-eap")
    }*/



  }

}


apply("project.gradle.kts")

allprojects {

  repositories {
    mavenLocal()

    google()
    jcenter()
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
/*    maven {
      setUrl("https://maven.google.com/")
    }*/
    mavenCentral()

    maven {
      setUrl( "https://jitpack.io")
    }


    /*   maven {
         setUrl("https://dl.bintray.com/kotlin/kotlin-eap")
       }*/
  }


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

