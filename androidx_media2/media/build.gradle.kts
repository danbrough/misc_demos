plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

android {

  // buildToolsVersion("30.0.2")

  compileSdkVersion(ProjectVersions.SDK_VERSION)

  defaultConfig {
    minSdkVersion(ProjectVersions.MIN_SDK_VERSION)
    targetSdkVersion(ProjectVersions.SDK_VERSION)

    //versionCode = ProjectVersions.BUILD_VERSION
    //versionName = ProjectVersions.VERSION_NAME

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

  }

  buildFeatures {
    aidl = true
  }

  buildTypes {

    getByName("release") {
      isMinifyEnabled = true
      proguardFiles(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          "proguard-rules.pro"
      )
    }

  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
    /*  outputs.upToDateWhen {
        false
      }*/
  }
}


dependencies {

  // implementation(AndroidX.coreKtx)
  implementation(Kotlin.stdlib.jdk8)
  implementation(AndroidX.annotation)
  implementation(AndroidX.lifecycle.liveDataKtx)

  implementation(AndroidX.media2.common)
  implementation(AndroidX.media2.session)
  //implementation(AndroidX.media2.session)
//  implementation(project(":session"))



  //implementation(AndroidX.media2.player)
  //implementation(project(":exomedia2"))
  implementation("androidx.core:core-ktx:_")


  api(AndroidX.concurrent.futures)
  //api(AndroidX.media2.exoplayer)
  implementation(Google.android.material)
  implementation("com.google.guava:guava:_")
  implementation("com.github.danbrough.androidutils:misc:_")
  implementation("com.github.danbrough.androidutils:logging_core:_")
  api("com.github.bumptech.glide:glide:_")
  kapt("com.github.bumptech.glide:compiler:_")
  val exo_vanilla = false
  val exo_package =
      if (exo_vanilla) "com.google.android.exoplayer" else "com.github.danbrough.exoplayer"
  val exo_version = if (exo_vanilla) "2.12.0" else "2.14.1-dan01" // "2.12.0-dan17"//"2.11.8-dan02"

  if (exo_vanilla) {
    implementation("$exo_package:exoplayer-core:$exo_version")
    implementation("$exo_package:exoplayer-smoothstreaming:$exo_version")
    implementation("$exo_package:exoplayer-ui:$exo_version")
    implementation("$exo_package:exoplayer-hls:$exo_version")
    implementation("$exo_package:exoplayer-dash:$exo_version")

    implementation("$exo_package:exoplayer-media2:$exo_version")

    implementation("$exo_package:extension-cast:$exo_version")
  } else {

    implementation("$exo_package:exoplayer-core:$exo_version")
    implementation("$exo_package:exoplayer-smoothstreaming:$exo_version")
    implementation("$exo_package:exoplayer-ui:$exo_version")
    implementation("$exo_package:exoplayer-hls:$exo_version")
    implementation("$exo_package:exoplayer-dash:$exo_version")


    //implementation(project(":exomedia2"))
    // implementation("$exo_package:extension-mediasession:$exo_version")
    //implementation("$exo_package:extension-okhttp:$exo_version")
    // implementation("$exo_package:extension-ffmpeg:$exo_version")
    // implementation("$exo_package:extension-ffmpeg:$exo_version")
/*    implementation("$exo_package:extension-media2:$exo_version"){
      exclude(group="androidx.media2")
    }*/
    implementation("$exo_package:extension-media2:$exo_version")
    implementation("$exo_package:extension-cast:$exo_version")
    implementation("$exo_package:extension-opus:$exo_version")
    implementation("$exo_package:extension-flac:$exo_version")
  }


  //implementation("com.github.danbrough.exoplayer:extension-media2:2.12.0-dan02")


  //implementation("androidx.appcompat:appcompat:_")
  testImplementation("junit:junit:_")
  androidTestImplementation("androidx.test.ext:junit:_")
  androidTestImplementation("androidx.test.espresso:espresso-core:_")
}
