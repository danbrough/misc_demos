import org.gradle.configurationcache.extensions.capitalized
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.targets
import org.jetbrains.kotlin.gradle.tasks.KotlinTest

object Common {


  @Suppress("UNCHECKED_CAST")
  fun <T : KotlinTarget> Project.createTarget(
    platform: Platform<T>,
    targetName: String = platform.name.toString(),
    conf: T.() -> Unit = {}
  ): T {
    println("Creatting target $targetName for platform $platform")
    val extn = kotlinExtension as KotlinMultiplatformExtension
    val preset: KotlinTargetPreset<T> =
      extn.presets.getByName(platform.name.toString()) as KotlinTargetPreset<T>
    return extn.targetFromPreset(preset, targetName, conf)
  }



}

object GoLib {

  fun Project.registerGoLibBuild(
    platform: PlatformNative<*>,
    goDir: File,
    outputDir: File,
    name: String = "golibBuild${platform.name.toString().capitalized()}"
  ) = tasks.register<Common_gradle.GolibTask>(name, platform, goDir).also {
    it {
      libDir.set(outputDir)
    }
  }

  fun Project.RegisterGreeting(name: String, greeting: String) =
    this.tasks.register<GreetingTask>(name) {
      this.greeting.set(greeting)
    }

}


abstract class GreetingTask : DefaultTask() {
  @get:Input
  @get:Optional
  abstract val greeting: Property<String?>

  @TaskAction
  fun greet() {
    println("hello from GreetingTask: ${greeting.orNull}")
  }
}



tasks.register("styleTest") {
  doLast {
    val out = project.serviceOf<StyledTextOutputFactory>().create("testOutput")
    StyledTextOutput.Style.values().forEach {
      out.style(it).println("This line has the style: $it")

      if (hasProperty("message")) {
        out.style(it).println("The message is: ${property("message")}")
      }
    }
  }
}

abstract class GolibTask @Inject constructor(
  private val platform: PlatformNative<*>, private val goDir: File
) : Exec() {

  @get:Input
  abstract val libDir: Property<File>

  init {
    group = BasePlugin.BUILD_GROUP
    // println("PLATFORM $platform  godir: $goDir: libDir: ${libDir.orNull}")

    environment("PLATFORM", platform.name.toString())


    val outputDir = project.buildDir.resolve("lib/$platform")
    assert(outputDir.mkdirs())


    inputs.files(project.fileTree(goDir) {
      include("**/*.go")
      include("**/*.c")
      include("**/*.h")
      include("**/*.mod")
    })


    val outputFiles = listOf("libgodemo.so", "libgodemo.h").map { outputDir.resolve(it) }
    outputs.files(outputFiles)
    workingDir(goDir)

    val commandEnvironment = BuildEnvironment.environment(platform)
    environment(commandEnvironment)

    commandLine(
      listOf(
        BuildEnvironment.goBinary,
        "build", "-v",//"-x",
        "-trimpath",
        "-ldflags", "-linkmode 'external'",
        "-buildmode=c-shared",
        "-o", outputFiles[0],
        "."
      )
    )

    val out = project.serviceOf<StyledTextOutputFactory>().create("golibOutput")

    doFirst {
      out.style(StyledTextOutput.Style.Info).println("Building golib for $platform")
      out.style(StyledTextOutput.Style.ProgressStatus).println("environment: $commandEnvironment")
    }
    doLast {
      if (didWork)
        out.style(StyledTextOutput.Style.Success).println("Finished building golib for $platform")
    }
  }

}


