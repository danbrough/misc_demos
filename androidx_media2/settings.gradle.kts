import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies


pluginManagement {
  repositories {
    jcenter()
    gradlePluginPortal()
  }
}



buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard:dependencies:+")
}


bootstrapRefreshVersionsAndDependencies()


/*import de.fayard.dependencies.DependenciesSetup



buildscript {
  repositories {
    mavenLocal() // Only necessary for testing
    gradlePluginPortal()
    mavenCentral()
  }
  dependencies.classpath("de.fayard:dependencies:+")
}

DependenciesSetup.bootstrapRefreshVersionsAndDependencies(settings)


include ":app"
include ':media'*/
include(":app", ":media",":session",":exomedia2")
rootProject.name = "media2_demo"

