import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

include(":ipfs")


buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard:dependencies:+")
}


bootstrapRefreshVersionsAndDependencies()

include(":app",":ipfs_kotlin")

rootProject.name = "ipfs_demo"

