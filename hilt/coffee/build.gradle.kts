plugins {
  id("java-library")
  id("kotlin")
  kotlin("kapt")
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.register<JavaExec>("coffeeApp") {
  main = "com.example.dagger.CoffeeApp"
  classpath = sourceSets["test"].runtimeClasspath
}


defaultTasks("coffeeApp")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "1.8"
    languageVersion = "1.4"
    // freeCompilerArgs = listOf("-Xjvm-default=enable")
    freeCompilerArgs += listOf(
        "-Xopt-in=kotlinx.serialization.InternalSerializationApi"
    )
  }
}
dependencies {
  implementation("org.slf4j:slf4j-api:_")
  kapt(Google.dagger.compiler)
  implementation(Google.dagger)

  testImplementation("ch.qos.logback:logback-core:_")
  testImplementation("ch.qos.logback:logback-classic:_")


  testImplementation(Kotlin.test.junit)

}