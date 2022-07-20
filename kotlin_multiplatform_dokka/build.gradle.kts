import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.dokka")
  id("com.android.library")
  `maven-publish`
}

group = "dokka.test"
version = "0.0.1"

buildscript {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

repositories {
  mavenCentral()
  google()
}

kotlin {
  jvm()

  android()

  linuxX64()
  macosX64()

  sourceSets {
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
  }

  val posixMain by sourceSets.creating {
  }

  targets.withType<KotlinNativeTarget>() {
    compilations["main"].defaultSourceSet.dependsOn(posixMain)
  }

}

tasks.withType<AbstractTestTask>() {
  testLogging {
    events = setOf(
      TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED
    )
    exceptionFormat = TestExceptionFormat.FULL
    showStandardStreams = true
    showStackTraces = true
  }
  outputs.upToDateWhen {
    false
  }
}

tasks.withType(KotlinCompile::class) {
  kotlinOptions {
    jvmTarget = "11"
  }
}


tasks.dokkaHtml.configure {
  outputDirectory.set(buildDir.resolve("dokka"))
}


val javadocJar by tasks.registering(Jar::class) {
  archiveClassifier.set("javadoc")
  from(tasks.dokkaHtml)
}

publishing {

  repositories {
    maven(project.buildDir.resolve("m2").toURI()) {
      name = "m2"
    }
  }

  publications.forEach {
    if (it !is MavenPublication) {
      return@forEach
    }

    // We need to add the javadocJar to every publication
    // because otherwise maven is complaining.
    // It is not sufficient to only have it in the "root" folder.
    it.artifact(javadocJar)
  }
}

android {

  compileSdk = 33
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  namespace = project.group.toString()

  defaultConfig {
    minSdk = 23
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  signingConfigs.register("release") {
    storeFile = File(System.getProperty("user.home"), ".android/keystore")
    keyAlias = "keyAlias"
    storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
    keyPassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
  }


  lint {
    abortOnError = false
  }


  buildTypes {

    getByName("debug") {
      //debuggable(true)
    }

    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
      )
      signingConfig = signingConfigs.getByName("release")
    }
  }

}