import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard:dependencies:+")
}


bootstrapRefreshVersionsAndDependencies()

include(":app",  ":compose_app",":domain")

rootProject.name = "bottom_sheet_demo"

//include(":menu")
//project(":menu").projectDir = file("../androidutils/menu")
