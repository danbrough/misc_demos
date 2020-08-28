import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard:dependencies:+")
}


bootstrapRefreshVersionsAndDependencies()

include(":app")

rootProject.name = "menu_dsl"

