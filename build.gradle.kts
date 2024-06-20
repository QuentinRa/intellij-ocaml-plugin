import org.jetbrains.kotlin.konan.properties.hasProperty
import java.util.Properties

// NOTE:
//
// The build is controlled by "gradle.properties" variables
// and the file ".config/gradle-{platformVersion}.properties"
//
// You won't have to edit this file unless you want to modify
// the build "framework" itself.

// NOTE:
// Refer to gradle/libs.versions.toml for versions
// <!> The project may not compile if you don't update gradleIntelliJPlugin
plugins {
  id("java")
  alias(libs.plugins.kotlin)
  alias(libs.plugins.gradleIntelliJPlugin)
}

// Load Configuration File
val platformVersion: String by project
val configFile = file(".config/gradle-${platformVersion}.properties")
if (!configFile.exists()) {
  throw GradleException("Configuration file not found: ${configFile.absolutePath}")
}
val configProperties = Properties()
configFile.inputStream().use { input -> configProperties.load(input) }

// Loading Variables
val majorVersion: String by project
val minorVersion: String by project
val patchVersion: String by project
val platformType: String by project
val targetVersion: String =
  if (!configProperties.hasProperty("ideRelease"))
    configProperties.getProperty("ideVersion")
  else
    platformType+"-"+configProperties.getProperty("ideRelease")
val indicesVersion : String = configProperties.getProperty("indicesVersion")
val psiViewerPluginVersion : String = configProperties.getProperty("psiViewerPluginVersion")
val pluginSinceBuild : String = configProperties.getProperty("pluginSinceBuild")
val pluginUntilBuild : String = configProperties.getProperty("pluginUntilBuild")
val allPlatforms : String by project
val useJava = when (platformType) {
  "IC", "IU" -> true
  "CL", "PY", "GO" -> false
  else -> throw IllegalStateException("Unexpected IDE name '"+platformType+"'")
}

group = "com.ocaml"
version = "$majorVersion.$minorVersion.$patchVersion-$platformVersion"

repositories {
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set(targetVersion)
  // type.set("IC") // Target IDE Platform

  plugins.set(listOf(/* Plugin Dependencies */
    "com.jetbrains.hackathon.indices.viewer:$indicesVersion",
    "PsiViewer:$psiViewerPluginVersion"
  ))
  if (useJava) plugins.add("java")

  sandboxDir.set("$buildDir/idea-sandbox-$platformVersion-$platformType")

  sourceSets["main"].java.srcDirs("src/main/gen")
  if (allPlatforms.isNotBlank()) {
    for (platform in allPlatforms.splitToSequence(",")) {
      if (platform <= platformVersion)
        sourceSets["main"].kotlin.srcDirs("src/$platform/$platform+/kotlin")
      if (platform > platformVersion)
        sourceSets["main"].kotlin.srcDirs("src/$platform/$platform-/kotlin")
    }
  }
  if (useJava) {
    sourceSets["main"].kotlin.srcDirs("src/deps/idea/kotlin")
  }

  idea {
    module {
      generatedSourceDirs.add(file("src/main/gen"))
    }
  }
}

tasks {

  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
      jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
  }

  patchPluginXml {
    sinceBuild.set(pluginSinceBuild)
    untilBuild.set(pluginUntilBuild)
    changeNotes.set(
      """
      <ul>
        <li>Can use CTRL-P to display parameters given a function (#38).</li>
        <li>Can use type inference with ocaml modules (#98).</li>
        <li>Fixing class not found when using a String</li>
        <li>See <a href="https://github.com/QuentinRa/intellij-ocaml/blob/main/CHANGELOG.md">CHANGELOG.md</a></li>
      <ul>
      """.trimIndent()
    )
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
