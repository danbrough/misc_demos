plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  kotlin("android.extensions")
  id("androidx.navigation.safeargs.kotlin")

}



android {

  repositories {
    mavenLocal()
  }

  compileSdkVersion(ProjectVersions.SDK_VERSION)

  defaultConfig {
    //buildToolsVersion("30.0.2")

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


  kotlinOptions {
    jvmTarget = "1.8"
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
  implementation(AndroidX.lifecycle.runtimeKtx)
  implementation(AndroidX.lifecycle.liveDataKtx)
  implementation(AndroidX.coreKtx)
  implementation(Kotlin.stdlib.jdk8)
  implementation("commons-io:commons-io:2.7")

  implementation("ipfs.gomobile:core:0.8.0-dan03@aar")


  //implementation(Libs.slf4j)
  implementation("com.github.danbrough.androidutils:menu:_")
  implementation(project(":ipfs_kotlin"))
  //implementation("com.github.ligi:ipfs-api-kotlin:0.5")

  implementation("com.github.ipfs:java-ipfs-http-client:1.3.3")

  implementation(Square.okHttp3.okHttp)
  implementation("org.slf4j:slf4j-api:_")

//  implementation(project(":menu"))

  androidTestImplementation(Testing.junit4)

  androidTestImplementation(AndroidX.test.core)
  androidTestImplementation(AndroidX.test.runner)
  androidTestImplementation(AndroidX.test.rules)




  testImplementation("ch.qos.logback:logback-classic:_")
  testImplementation("ch.qos.logback:logback-core:_")


  implementation("com.github.danbrough.androidutils:slf4j:_")

  //implementation(Libs.slf4j_android)


  implementation(AndroidX.navigation.fragmentKtx)
  implementation(AndroidX.navigation.uiKtx)
  implementation(AndroidX.constraintLayout)
  implementation(AndroidX.preferenceKtx)

  implementation(Google.android.material)


}
