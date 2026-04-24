import net.fabricmc.loom.LoomGradlePlugin
import net.fabricmc.loom.LoomNoRemapGradlePlugin
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
    kotlin("jvm") version "2.3.20"
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
    `maven-publish`
}

val minecraftVersion = stonecutter.current.version.substringBefore('-')
val noMappings = stonecutter.eval(minecraftVersion, ">=26.1")

version = "${rootProject.property("mod_version")}+$minecraftVersion"
group = rootProject.property("maven_group") as String
base.archivesName.set(rootProject.property("archives_base_name") as String)

if (noMappings) {
    apply<LoomNoRemapGradlePlugin>()

    configurations.api.get().extendsFrom(configurations.create("modApi"))
    configurations.implementation.get().extendsFrom(configurations.create("modImplementation"))
    configurations.compileOnly.get().extendsFrom(configurations.create("modCompileOnly"))
    configurations.runtimeOnly.get().extendsFrom(configurations.create("modRuntimeOnly"))
} else {
    apply<LoomGradlePlugin>()
}

val loom = the<LoomGradleExtensionAPI>()

repositories {
    mavenCentral()

    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    "minecraft"("com.mojang:minecraft:$minecraftVersion")

    if (!noMappings) {
        "mappings"(loom.officialMojangMappings())
    }

    "modImplementation"("net.fabricmc:fabric-loader:${property("loader_version")}")
    "modImplementation"("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    "modImplementation"("maven.modrinth:replaymod:${property("deps.replaymod")}")

    compileOnly(kotlin("stdlib-jdk8"))
}

tasks {
    jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }

    processResources {
        inputs.property("version", project.version)
        inputs.property("minecraftVersionDependency", project.property("minecraft_version_dependency"))

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to project.version,
                    "minecraftVersionDependency" to project.property("minecraft_version_dependency"),
                ),
            )
        }
    }

    withType<JavaCompile>().configureEach {
        options.release.set(if (noMappings) 25 else 21)
    }

    val copyToRoot =
        register<Copy>("copyToRoot") {
            if (noMappings) {
                from(jar.get().archiveFile)
            } else {
                from(named<RemapJarTask>("remapJar").get().archiveFile)
            }
            into(rootProject.layout.buildDirectory.dir("libs"))
        }

    build {
        finalizedBy(copyToRoot)
    }
}

kotlin {
    jvmToolchain(if (noMappings) 25 else 21)
}

java.toolchain.languageVersion.set(
    JavaLanguageVersion.of(if (noMappings) 25 else 21),
)
