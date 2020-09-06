plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  kotlin("android.extensions")
  id("androidx.navigation.safeargs.kotlin")
}



android {


  compileSdkVersion(ProjectVersions.SDK_VERSION)

  defaultConfig {
    buildToolsVersion("30.0.2")

    minSdkVersion(ProjectVersions.MIN_SDK_VERSION)
    targetSdkVersion(ProjectVersions.SDK_VERSION)
    versionCode = ProjectVersions.BUILD_VERSION
    versionName = ProjectVersions.VERSION_NAME
    multiDexEnabled = true
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

    vectorDrawables {
      useSupportLibrary = true
    }
  }

  lintOptions {
    isAbortOnError = false
  }


  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }


  kotlinOptions {
    jvmTarget = "1.8"
  }

  kapt {
    correctErrorTypes = true
  }

}

tasks.withType<Test> {
  useJUnit()

  testLogging {
    events("standardOut", "started", "passed", "skipped", "failed")
    showStandardStreams = true
    outputs.upToDateWhen {
      false
    }
  }
}

dependencies {

  //implementation(project(":media"))
  implementation(AndroidX.lifecycle.runtimeKtx)
  implementation(AndroidX.lifecycle.liveDataKtx)
  implementation(AndroidX.lifecycle.viewModelKtx)
  implementation(AndroidX.coreKtx)
  implementation(Kotlin.stdlib.jdk8)

  implementation(AndroidX.navigation.fragmentKtx)
  implementation(AndroidX.navigation.uiKtx)
  implementation(AndroidX.constraintLayout)
  implementation(AndroidX.preferenceKtx)

  implementation(Google.android.material)

  val media_version = "1.1.0-alpha01"
  //implementation("androidx.media2:media2-exoplayer:$media_version")
  //implementation("androidx.media2:media2-player:$media_version")
  implementation("androidx.media2:media2-session:$media_version")
  implementation("androidx.media2:media2-common:$media_version")

  implementation("com.google.guava:guava:29.0-android")


  api(AndroidX.concurrent.futures)
  //implementation("com.github.danbrough.exoplayer:extension-media2:2.12.0-dan02")

  //implementation(Libs.slf4j_android)
  implementation("org.slf4j:slf4j-api:_")
  //implementation(Libs.slf4j)
  implementation("com.github.danbrough.androidutils:menu:_")
  implementation("com.github.danbrough.androidutils:slf4j:_")

  val exo_vanilla = false
  val exo_version = if (exo_vanilla) "2.11.8" else "2.11.8-dan02"
  val exo_package =
    if (exo_vanilla) "com.google.android.exoplayer" else "com.github.danbrough.exoplayer"

  if (exo_vanilla) {

    implementation("$exo_package:exoplayer-core:$exo_version")
    implementation("$exo_package:exoplayer-smoothstreaming:$exo_version")
    implementation("$exo_package:exoplayer-ui:$exo_version")
    implementation("$exo_package:exoplayer-hls:$exo_version")
    implementation("$exo_package:extension-cast:$exo_version")
  } else {

    implementation("$exo_package:library-core:$exo_version")
    implementation("$exo_package:library-smoothstreaming:$exo_version")
    implementation("$exo_package:library-ui:$exo_version")
    implementation("$exo_package:library-hls:$exo_version")


    // implementation("$exo_package:extension-mediasession:$exo_version")
    //implementation("$exo_package:extension-okhttp:$exo_version")
    implementation("$exo_package:extension-cast:$exo_version")
    implementation("$exo_package:extension-opus:$exo_version")
    implementation("$exo_package:extension-flac:$exo_version")
  }

  implementation(Square.okHttp3.okHttp)


//  implementation(project(":menu"))

  androidTestImplementation(Testing.junit4)

  androidTestImplementation(AndroidX.test.core)
  androidTestImplementation(AndroidX.test.runner)
  androidTestImplementation(AndroidX.test.rules)

  androidTestImplementation("com.github.danbrough.androidutils:slf4j:_")


/*  testImplementation("ch.qos.logback:logback-classic:_")
  testImplementation("ch.qos.logback:logback-core:_")

  kapt("com.google.dagger:hilt-android-compiler:_")
  implementation("com.google.dagger:hilt-android:_")

  // For instrumentation tests
  androidTestImplementation("com.google.dagger:hilt-android-testing:_")

  // For local unit tests
  testImplementation("com.google.dagger:hilt-android-testing:_")*/


}
