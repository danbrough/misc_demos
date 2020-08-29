import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard:dependencies:+")
}


bootstrapRefreshVersionsAndDependencies()

include(":app")

rootProject.name = "compose_demo"

//include(":menu")
//project(":menu").projectDir = file("../androidutils/menu")