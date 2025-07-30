
plugins {
  id("java-library")
  id("maven-publish")
  id("eclipse")
  id("com.gtnewhorizons.gtnhconvention")
}

// Project properties
group = "rc55.mc.cauldronpp"
version = "0.0.1-1.7.10"

// Set the toolchain version to decouple the Java we run Gradle with from the Java used to compile and run the mod
java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
  // Generate sources and javadocs jars when building and publishing
  withSourcesJar()
  withJavadocJar()
}

// Most RFG configuration lives here, see the JavaDoc for com.gtnewhorizons.retrofuturagradle.MinecraftExtension
minecraft {
  mcVersion.set("1.7.10")

  // Username for client run configurations
  username.set("redColmula55")

  // Generate a field named VERSION with the mod version in the injected Tags class
  injectedTags.put("VERSION", project.version)

  // If you need the old replaceIn mechanism, prefer the injectTags task because it doesn't inject a javac plugin.
  // tagReplacementFiles.add("RfgExampleMod.java")

  // Enable assertions in the mod's package when running the client or server
  extraRunJvmArguments.add("-ea:${project.group}")

  // If needed, add extra tweaker classes like for mixins.
  extraTweakClasses.add("org.spongepowered.asm.launch.MixinTweaker")

  // Exclude some Maven dependency groups from being automatically included in the reobfuscated runs
  groupsToExcludeFromAutoReobfMapping.addAll("com.diffplug", "com.diffplug.durian", "net.industrial-craft")
}

// Generates a class named rfg.examplemod.Tags with the mod version in it, you can find it at
tasks.injectTags.configure {
  outputClassName.set("${project.group}.Tags")
}

// Put the version from gradle into mcmod.info
tasks.processResources.configure {
  val projVersion = project.version.toString() // Needed for configuration cache to work
  inputs.property("version", projVersion)

  filesMatching("mcmod.info") {
    expand(mapOf("modVersion" to projVersion))
  }
}

// Add an access tranformer
tasks.deobfuscateMergedJarToSrg.configure {accessTransformerFiles.from("src/main/resources/META-INF/cauldronpp_at.cfg")}

// Dependencies
repositories {
  maven {
    name = "OvermindDL1 Maven"
    url = uri("https://gregtech.overminddl1.com/")
  }
  maven {
    name = "GTNH Maven"
    url = uri("https://nexus.gtnewhorizons.com/repository/public/")
  }
  gradlePluginPortal()
  mavenCentral()
  mavenLocal()
}

dependencies {
  //api("com.github.GTNewHorizons:GTNHLib:0.7.0:dev")
  // Adds NotEnoughItems and its dependencies (CCL&CCC) to runClient/runServer
  runtimeOnlyNonPublishable("com.github.GTNewHorizons:NotEnoughItems:2.3.39-GTNH:dev")
  // Example: grab the ic2 jar from curse maven and deobfuscate
  // api(rfg.deobf("curse.maven:ic2-242638:2353971"))
  // Example: grab the ic2 jar from libs/ in the workspace and deobfuscate
  // api(rfg.deobf(project.files("libs/ic2.jar")))
}
