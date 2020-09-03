plugins {
  id("com.android.library")
  id("kotlin-android")
}
/*
Internal Error occurred while analyzing this expression:
com.intellij.openapi.vfs.InvalidVirtualFileAccessException:
Accessing invalid virtual file: file:///home/dan/workspace/android/demos/androidx_media2/media/build/generated/ap_generated_sources/debug/out;
original:1175356; found:-; File.exists()=false
	at com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl.handleInvalidDirectory(VirtualDirectoryImpl.java:191)
	at com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl.doFindChild(VirtualDirectoryImpl.java:112)
	at com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl.findChild(VirtualDirectoryImpl.java:78)
	at com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl.findChild(VirtualDirectoryImpl.java:478)
	at com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl.findChild(VirtualDirectoryImpl.java:45)
	at org.jetbrains.kotlin.idea.modules.ModuleHighlightUtil2.lambda$getModuleDescriptor$0(ModuleHighlightUtil2.java:85)
	at kotlin.collections.ArraysKt___ArraysKt.singleOrNull(_Arrays.kt:3009)
	at org.jetbrains.kotlin.idea.modules.ModuleHighlightUtil2.getModuleDescriptor(ModuleHighlightUtil2.java:84)
	at org.jetbrains.kotlin.idea.modules.IdeJavaModuleResolver.findJavaModule(IdeJavaModuleResolver.kt:17)
	at org.jetbrains.kotlin.idea.modules.IdeJavaModuleResolver.checkAccessibility(IdeJavaModuleResolver.kt:22)
	at org.jetbrains.kotlin.resolve.jvm.checkers.JvmModuleAccessibilityChecker.diagnosticFor(JvmModuleAccessibilityChecker.kt:71)
	at org.jetbrains.kotlin.resolve.jvm.checkers.JvmModuleAccessibilityChecker.check(JvmModuleAccessibilityChecker.kt:57)
	at org.jetbrains.kotlin.resolve.calls.tower.KotlinToResolvedCallTransformer.runCallCheckers(KotlinToResolvedCallTransformer.kt:243)
	at org.jetbrains.kotlin.resolve.calls.tower.ResolvedAtomCompleter.completeResolvedCall(ResolvedAtomCompleter.kt:143)
	at org.jetbrains.kotlin.resolve.calls.tower.KotlinToResolvedCallTransformer.transformAndReport(KotlinToResolvedCallTransformer.kt:158)
	at org.jetbrains.kotlin.resolve.calls.tower.PSICallResolver.convertToOverloadResolutionResults(PSICallResolver.kt:234)
	at o...

	Cannot access 'java.lang.Comparable' which is a supertype of 'Build_gradle'. Check your module classpath for missing or conflicting dependencies
 */
android {

  buildToolsVersion("30.0.2")

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
  api(AndroidX.media2.exoplayer)
  api(AndroidX.media2.player)

  implementation("com.google.guava:guava:29.0-android")


  api(AndroidX.concurrent.futures)
  api(AndroidX.media2.common)
  implementation("com.github.danbrough.exoplayer:extension-media2:2.12.0-dan02")

  implementation("org.jetbrains.kotlin:kotlin-stdlib:_")
  implementation("androidx.core:core-ktx:1.3.1")
  implementation("androidx.appcompat:appcompat:1.2.0")
  testImplementation("junit:junit:4.+")
  androidTestImplementation("androidx.test.ext:junit:1.1.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}