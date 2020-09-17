/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


plugins {
  id("com.android.library")
}


dependencies {
  implementation("org.slf4j:slf4j-api:_")
  compileOnly("androidx.versionedparcelable:versionedparcelable:1.1.1")
  compileOnly(AndroidX.media2.common)
  compileOnly("com.google.guava:guava:_")
  compileOnly(AndroidX.concurrent.futures)
}

android {
  compileSdkVersion(ProjectVersions.SDK_VERSION)

  defaultConfig {
    minSdkVersion(16)

  }
  buildFeatures {
    aidl = true
  }
}
