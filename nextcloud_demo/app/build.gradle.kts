plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdk = ProjectVersions.SDK_VERSION

  namespace = "danbroid.demo"

  defaultConfig {
    //applicationId "danbroid.demo"
    minSdk = ProjectVersions.MIN_SDK_VERSION
    targetSdk = ProjectVersions.SDK_VERSION
    versionCode = ProjectVersions.BUILD_VERSION
    versionName = ProjectVersions.VERSION_NAME
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }

  kotlinOptions {
    jvmTarget = ProjectVersions.KOTLIN_JVM_VERSION
  }

  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation("com.github.nextcloud:android-library:_")
// https://mvnrepository.com/artifact/org.apache.httpcomponents/httpclient
  //implementation("org.apache.httpcomponents:httpclient:_")
// https://mvnrepository.com/artifact/commons-httpclient/commons-httpclient
  implementation("commons-httpclient:commons-httpclient:_"){
    exclude("commons-logging","commons-logging")
  }

  implementation(AndroidX.core.ktx)
  implementation(AndroidX.appCompat)
  implementation(Google.android.material)
  implementation(AndroidX.constraintLayout)
  implementation(AndroidX.navigation.fragmentKtx)
  implementation(AndroidX.navigation.uiKtx)
  implementation("com.github.danbrough.androidutils:logging:_")
  testImplementation(Testing.junit4)
  androidTestImplementation(AndroidX.test.ext.junit)
  androidTestImplementation(AndroidX.test.espresso.core)
}