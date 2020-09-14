plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {

  // buildToolsVersion("30.0.2")

  compileSdkVersion(ProjectVersions.SDK_VERSION)

  defaultConfig {
    minSdkVersion(ProjectVersions.MIN_SDK_VERSION)
    targetSdkVersion(ProjectVersions.SDK_VERSION)

    versionCode = ProjectVersions.BUILD_VERSION
    versionName = ProjectVersions.VERSION_NAME

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

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
  api("org.slf4j:slf4j-api:_")
  api(AndroidX.media2.session)
  api(AndroidX.media2.common)

  //api(AndroidX.media2.exoplayer)
  implementation(Google.android.material)

  val exo_vanilla = false
  val exo_package =
    if (exo_vanilla) "com.google.android.exoplayer" else "com.github.danbrough.exoplayer"
  val exo_version = if (exo_vanilla) "2.12.0" else "2.12.0-dan13"//"2.11.8-dan02"

  if (exo_vanilla) {

    implementation("$exo_package:exoplayer-core:$exo_version")
    implementation("$exo_package:exoplayer-smoothstreaming:$exo_version")
    implementation("$exo_package:exoplayer-ui:$exo_version")
    implementation("$exo_package:exoplayer-hls:$exo_version")
    implementation("$exo_package:exoplayer-media2:$exo_version")

    implementation("$exo_package:extension-cast:$exo_version")
  } else {

    implementation("$exo_package:exoplayer-core:$exo_version")
    implementation("$exo_package:exoplayer-smoothstreaming:$exo_version")
    implementation("$exo_package:exoplayer-ui:$exo_version")
    implementation("$exo_package:exoplayer-hls:$exo_version")
    implementation("$exo_package:extension-media2:$exo_version")


    // implementation("$exo_package:extension-mediasession:$exo_version")
    //implementation("$exo_package:extension-okhttp:$exo_version")
    // implementation("$exo_package:extension-ffmpeg:$exo_version")
    // implementation("$exo_package:extension-ffmpeg:$exo_version")
    implementation("$exo_package:extension-cast:$exo_version")
    implementation("$exo_package:extension-opus:$exo_version")
    implementation("$exo_package:extension-flac:$exo_version")
  }
  implementation("com.google.guava:guava:_")


  api(AndroidX.concurrent.futures)
  api(AndroidX.media2.common)
  //implementation("com.github.danbrough.exoplayer:extension-media2:2.12.0-dan02")

  implementation("org.jetbrains.kotlin:kotlin-stdlib:_")
  implementation("androidx.core:core-ktx:_")
  implementation("androidx.appcompat:appcompat:_")
  testImplementation("junit:junit:_")
  androidTestImplementation("androidx.test.ext:junit:_")
  androidTestImplementation("androidx.test.espresso:espresso-core:_")
}