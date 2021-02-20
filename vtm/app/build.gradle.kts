plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
}


android {

  compileSdkVersion(ProjectVersions.SDK_VERSION)

  defaultConfig {
    minSdkVersion(ProjectVersions.MIN_SDK_VERSION)
    targetSdkVersion(ProjectVersions.SDK_VERSION)
    versionCode = ProjectVersions.BUILD_VERSION
    versionName = ProjectVersions.VERSION_NAME
    multiDexEnabled = true
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
    buildConfigField("String", "URI_SCHEME", "\"${Danbroid.URI_SCHEME}\"")
  }

  kapt {
    correctErrorTypes = true
  }

  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }

  buildFeatures {
    dataBinding = false
    viewBinding = true

  }

  kotlinOptions {
    jvmTarget = "1.8"
    //freeCompilerArgs = listOf("-Xjsr305=strict")
    freeCompilerArgs = mutableListOf("-Xopt-in=kotlin.ExperimentalStdlibApi",
        "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi").also {
      it.addAll(freeCompilerArgs)
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

  lintOptions {
    isAbortOnError = false
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.isReturnDefaultValues = true
  }


  useLibrary("android.test.runner")

  useLibrary("android.test.base")
  useLibrary("android.test.mock")
/*  configurations.all {
    println("CONF: $this")
    forEach {
      println("FOR EACH $it")
    }
  }*/

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


  testImplementation(Testing.junit4)
  testImplementation("ch.qos.logback:logback-core:_")
  testImplementation("ch.qos.logback:logback-classic:_")

implementation("org.mapsforge:vtm-gdx:0.15.0")

  //testImplementation("org.mockito:mockito-core:2.28.2")

//  implementation("com.mikepenz:iconics-core:_")
  implementation(AndroidX.appCompat)
        implementation ("org.maplibre.gl:android-sdk:_")
  implementation("com.mikepenz:iconics-core:_@aar")
  implementation("com.mikepenz:fontawesome-typeface:_@aar")
  implementation("com.mikepenz:google-material-typeface:_@aar")
  implementation("com.mikepenz:material-design-iconic-typeface:_@aar")
  implementation("org.jetbrains.kotlin:kotlin-reflect:_")
  //implementation("com.mikepenz:material-design-iconic-typeface:2.2.0.7-kotlin")
  //implementation("com.mikepenz:fontawesome-typeface:5.9.0.1-kotlin@aar")
  //implementation("com.mikepenz:google-material-typeface:3.0.1.5.original-kotlin@aar")

  implementation(Danbroid.utils.misc)
  implementation(Danbroid.utils.menu)
  implementation(Danbroid.utils.slf4j)

  implementation(Google.android.material)

  implementation(AndroidX.preferenceKtx)


  implementation(AndroidX.core.ktx)
  implementation(AndroidX.coordinatorLayout)

  implementation(AndroidX.fragmentKtx)

  //implementation(AndroidX.lifecycle.extensions)
  implementation(AndroidX.lifecycle.liveDataCoreKtx)
  implementation(AndroidX.lifecycle.liveDataKtx)
  implementation(AndroidX.lifecycle.viewModelKtx)
  implementation(AndroidX.lifecycle.runtimeKtx)
  implementation(AndroidX.navigation.fragmentKtx)
  implementation(AndroidX.navigation.uiKtx)
  implementation(AndroidX.recyclerView)

  androidTestImplementation(AndroidX.test.core)
  androidTestImplementation(AndroidX.test.rules)
  androidTestImplementation(AndroidX.test.runner)
  androidTestImplementation(AndroidX.test.ext.junit)
  androidTestImplementation(AndroidX.test.ext.truth)
  androidTestImplementation(Danbroid.utils.slf4j)

  testImplementation("ch.qos.logback:logback-core:_")
  testImplementation("ch.qos.logback:logback-classic:_")
  testImplementation("ch.qos.logback:logback-classic:_") {
    exclude(":slf4j")
    exclude(group = ":slf4j")
    exclude(group = "com.github.danbrough.androidutils", module = "slf4j")

    //exclude(project(":slf4j"))
    /*compile('org.apache.zookeeper:zookeeper:3.4.5') {
      exclude group: 'ch.qos.logback', module: 'logback-classic'
    }*/
  }
  testImplementation("ch.qos.logback:logback-core:_")

  testImplementation("com.google.truth:truth:_")
  testImplementation(Testing.junit4)

  testImplementation(Testing.roboElectric)
  testImplementation("androidx.core:core:_")
  // TODO(bcorso): This multidex dep shouldn't be required -- it's a dep for the generated code.
  testImplementation("androidx.multidex:multidex:_")
  testImplementation("com.google.dagger:hilt-android-testing:_")
  kaptTest("com.google.dagger:hilt-android-compiler:_")
}


