plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  kotlin("android.extensions")
//kotlin("org.jetbrains.kotlin:kotlin-android-extensions-runtime:")
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
    // multiDexEnabled = true
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

  }

  lintOptions {
    isAbortOnError = false
  }


  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }

  buildFeatures {
    dataBinding = false
    compose = false
  }

  kotlinOptions {
    jvmTarget = "1.8"
 //   useIR = true
    // freeCompilerArgs =  listOf("-Xallow-jvm-ir-dependencies", "-Xskip-prerelease-check")

  }

/*
  composeOptions {
    kotlinCompilerExtensionVersion = "1.0.0-alpha01"
    kotlinCompilerVersion = "1.4.0"
  }
*/


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
  implementation(AndroidX.lifecycle.runtimeKtx)
 // implementation(AndroidX.lifecycle.extensions)
  implementation(AndroidX.lifecycle.liveDataKtx)
  implementation(AndroidX.coreKtx)
  implementation(Kotlin.stdlib.jdk8)
  implementation(AndroidX.navigation.fragmentKtx)
  implementation(AndroidX.navigation.uiKtx)
  implementation(AndroidX.constraintLayout)
  implementation(AndroidX.preferenceKtx)

  implementation(Google.android.material)



  //implementation(Libs.slf4j_android)
  implementation("org.slf4j:slf4j-api:_")
  //implementation(Libs.slf4j)
  implementation("com.github.danbrough.androidutils:menu:_")
  implementation("com.github.danbrough.androidutils:slf4j:_")


  implementation(Square.okHttp3.okHttp)


//  implementation(project(":menu"))

  androidTestImplementation(Testing.junit4)

  androidTestImplementation(AndroidX.test.core)
  androidTestImplementation(AndroidX.test.runner)
  androidTestImplementation(AndroidX.test.rules)

  androidTestImplementation("com.github.danbrough.androidutils:slf4j:_")



  testImplementation("ch.qos.logback:logback-classic:_")
  testImplementation("ch.qos.logback:logback-core:_")






}
