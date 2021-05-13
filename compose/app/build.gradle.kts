plugins {
  id("com.android.application")
  id("kotlin-android")
}

android {
  compileSdkVersion(ProjectVersions.SDK_VERSION)

  defaultConfig {
    // applicationId "danbroid.composetest"
    minSdkVersion(ProjectVersions.MIN_SDK_VERSION)
    targetSdkVersion(ProjectVersions.SDK_VERSION)
    versionCode(ProjectVersions.BUILD_VERSION)
    versionName(ProjectVersions.VERSION_NAME)

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      //minifyEnabled false

      //proguardFiles getDefaultProguardFile ('proguard-android-optimize.txt'), 'proguard-rules.pro'
      consumerProguardFiles("consumer-rules.pro")

    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
    useIR = true
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = Libs.compose_version
  }
}

dependencies {

  implementation(AndroidX.core.ktx)
  implementation(AndroidX.appCompat)
  implementation(Google.android.material)
  implementation(AndroidX.compose.ui)
  implementation(AndroidX.compose.material)
  implementation(AndroidX.compose.runtime.liveData)
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha03")
  implementation("androidx.compose.ui:ui-tooling:_")
  implementation(AndroidX.lifecycle.runtimeKtx)
  testImplementation(Testing.junit4)
  androidTestImplementation(AndroidX.test.ext.junit)
  androidTestImplementation(AndroidX.test.espresso.core)

  implementation("org.slf4j:slf4j-api:_")
  implementation("com.github.danbrough.androidutils:slf4j:_")
  implementation("com.github.danbrough.androidutils:menu:_")
  implementation("androidx.activity:activity-compose:_")
}