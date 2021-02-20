plugins {
  id("org.jetbrains.dokka")
}

buildscript {

  dependencies {
    classpath("com.android.tools.build:gradle:_")
    // classpath("com.android.tools.build:gradle:4.1.0")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:_")
    classpath("org.jetbrains.dokka:dokka-gradle-plugin:_")
    classpath(Google.dagger.hilt.android.gradlePlugin)
    //classpath(AndroidX.navigation.safeArgsGradlePlugin)
  }

  repositories {
    google()
    jcenter()
  }
}



apply("project.gradle.kts")


allprojects {
  repositories {
    maven("https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
    google()
    jcenter()
    maven("https://jitpack.io")
  }

  tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
    dokkaSourceSets {
      configureEach {
        includes.from(file("README.md"))
      }
    }
  }

  configurations.all {
    if (name.contains("Test")) {
/*      resolutionStrategy.dependencySubstitution {
        substitute(project(":slf4j")).with(module("ch.qos.logback:logback-classic:1.2.3"))
      }*/
    }
//    resolutionStrategy.force("org.slf4j:slf4j-api:1.7.30")
  }
}

tasks.dokkaGfmMultiModule {
  outputDirectory.set(file("docs"))
}

subprojects {
  afterEvaluate {

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
      kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.4"
        // freeCompilerArgs = listOf("-Xjvm-default=enable")
        freeCompilerArgs += listOf(
            //  "-Xopt-in=kotlinx.serialization.InternalSerializationApi",
            "-Xopt-in=kotlinx.serialization.InternalSerializationApi",

            "-Xopt-in=kotlinx.coroutines.InternalCoroutinesApi",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=kotlin.ExperimentalStdlibApi"
        )
      }
    }

    (extensions.findByType(com.android.build.gradle.LibraryExtension::class)
        ?: extensions.findByType(com.android.build.gradle.AppExtension::class))?.apply {

      if (this is com.android.build.gradle.LibraryExtension) {

        val publishing =
            extensions.findByType(PublishingExtension::class.java) ?: return@afterEvaluate

        val sourcesJar by tasks.registering(Jar::class) {
          archiveClassifier.set("sources")
          from(sourceSets.getByName("main").java.srcDirs)
        }


        lintOptions {
          isAbortOnError = false
        }

        compileOptions {
          sourceCompatibility = ProjectVersions.JAVA_VERSION
          targetCompatibility = ProjectVersions.JAVA_VERSION
        }

        afterEvaluate {

          publishing.apply {
            val projectName = name
            publications {
              val release by registering(MavenPublication::class) {
                /*components.forEach {
                  println("Publication component: ${it.name}")
                }*/
                from(components["release"])
                artifact(sourcesJar.get())
                artifactId = projectName
                groupId = ProjectVersions.GROUP_ID
                version = ProjectVersions.VERSION_NAME
              }
            }
          }
        }
      }
    }
  }
}
