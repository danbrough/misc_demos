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

    minSdkVersion(ProjectVersions.MIN_SDK_VERSION)
    targetSdkVersion(ProjectVersions.SDK_VERSION)
    applicationId = "danbroid.exoservice"
    versionCode = ProjectVersions.BUILD_VERSION
    versionName = ProjectVersions.VERSION_NAME
    multiDexEnabled = true
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")


  }

  compileOptions {
    sourceCompatibility = ProjectVersions.JAVA_VERSION
    targetCompatibility = ProjectVersions.JAVA_VERSION
  }

  dataBinding {
    isEnabled = false
  }

  androidExtensions {
    isExperimental = true
  }

  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "1.8"
      //freeCompilerArgs = listOf("-Xjsr305=strict")
    }
  }

  kotlin.sourceSets.all {
    setOf(
      "kotlinx.coroutines.ExperimentalCoroutinesApi",
      "kotlinx.coroutines.FlowPreview"
    ).forEach {
      languageSettings.useExperimentalAnnotation(it)
    }
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
    unitTests.isIncludeAndroidResources = true
    /*  unitTests.isReturnDefaultValues = true
      unitTests.isIncludeAndroidResources = true*/
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

/*
configurations.all {
  resolutionStrategy.force "com.squareup.okhttp3:okhttp:$okhttp_version"
}

project.afterEvaluate {
  android.applicationVariants.all { variant ->
    task "installRun${variant.name.capitalize()}"(type: Exec, dependsOn: "install${variant.name.capitalize()}", group: "run") {
      commandLine = ["adb", "shell", "monkey", "-p", variant.applicationId + " 1"]
      doLast {
        println "Launching ${variant.applicationId}"
      }
    }
  }
}
*/

dependencies {

  implementation(project(":media"))
  implementation(Libs.lifecycle_runtime_ktx)
  implementation(Libs.lifecycle_extensions)
  implementation(Libs.lifecycle_livedata_ktx)
  implementation(Libs.lifecycle_viewmodel_ktx)
  implementation(Libs.gson)
  implementation(Libs.core_ktx)
  implementation(Libs.okhttp)

  //sliding panel library
  implementation(Libs.library)

  implementation(Libs.kotlinx_coroutines_android)
  implementation(Libs.kotlin_stdlib_jdk8)

  implementation(Libs.slf4j_api)
  implementation(Libs.slf4j)
  testImplementation(Libs.logback_classic)
  testImplementation(Libs.logback_core)

  implementation(Libs.navigation_fragment_ktx)
  implementation(Libs.navigation_ui_ktx)
  implementation(Libs.constraintlayout)
  implementation(Libs.preference_ktx)
  implementation(Libs.material)
  implementation(Libs.resource)
  kapt(Libs.com_github_bumptech_glide_compiler)
  implementation(Libs.glide)


  androidTestImplementation(Libs.androidx_test_core)
  androidTestImplementation(Libs.androidx_test_rules)
  androidTestImplementation(Libs.androidx_test_runner)

  testImplementation(Libs.junit)
  testImplementation(Libs.logback_classic)
  testImplementation(Libs.logback_core)
  testImplementation(Libs.androidx_test_core)
  testImplementation(Libs.mockito_core)
  testImplementation(Libs.robolectric)


}
