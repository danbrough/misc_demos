plugins {
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
  kotlin("android.extensions")
}


android {
  compileSdkVersion(ProjectVersions.SDK_VERSION)
  defaultConfig {
    minSdkVersion(ProjectVersions.MIN_SDK_VERSION)
    targetSdkVersion(ProjectVersions.SDK_VERSION)
    versionCode = ProjectVersions.BUILD_VERSION
    versionName = ProjectVersions.VERSION_NAME
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

    javaCompileOptions {
      annotationProcessorOptions {
        argument("room.schemaLocation", "$projectDir/schemas")
        argument("room.incremental", "true")
        argument("room.expandProjection", "true")
      }
    }

  }
  androidExtensions {
    isExperimental = true
  }
  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
      //freeCompilerArgs = listOf("-Xjsr305=strict")
    }
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

  testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.isReturnDefaultValues = true
  }

/*  sourceSets {
    getByName("main").java.srcDir("../jvm_src/domain/kotlin")
    getByName("test").java.srcDir("../jvm_src/domain/kotlin")
  }*/

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

  api(Libs.slf4j_api)
  api(Libs.kotlin_stdlib_jdk8)
  api(Libs.kotlinx_coroutines_android)
  api("org.jetbrains.kotlin:kotlin-reflect:1.4.0")


  api(Libs.gson)
  api(Libs.core_ktx)
  api(Libs.okhttp)

  kapt(Libs.room_compiler)
  api(Libs.room_runtime)
  api(Libs.glide)
  api("com.github.danbrough.androidutils:misc:1.1.0-beta06")

  val exo_vanilla = false
  val exo_version = if (exo_vanilla) "2.11.0" else Versions.com_github_danbrough_exoplayer
  val exo_package =
    if (exo_vanilla) "com.google.android.exoplayer" else "com.github.danbrough.exoplayer"

  if (exo_vanilla) {
    implementation("$exo_package:exoplayer-core:$exo_version")
    implementation("$exo_package:exoplayer-smoothstreaming:$exo_version")
    implementation("$exo_package:exoplayer-ui:$exo_version")
    implementation("$exo_package:extension-mediasession:$exo_version")
    implementation("$exo_package:extension-okhttp:$exo_version")
    implementation("$exo_package:extension-cast:$exo_version")
  } else {

    implementation("$exo_package:library-core:$exo_version")
    implementation("$exo_package:library-smoothstreaming:$exo_version")
    implementation("$exo_package:library-ui:$exo_version")
    // implementation("$exo_package:extension-mediasession:$exo_version")
    //implementation("$exo_package:extension-okhttp:$exo_version")
    implementation("$exo_package:extension-cast:$exo_version")
    implementation("$exo_package:extension-opus:$exo_version")
    implementation("$exo_package:extension-flac:$exo_version")
  }


  testImplementation(Libs.junit)
  testImplementation(Libs.androidx_test_core)
  testImplementation(Libs.logback_core)
  testImplementation(Libs.logback_classic)
  //testImplementation(Libs.kxml2_min)
  testImplementation(Libs.robolectric)

}


