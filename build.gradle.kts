import com.apehum.replayaudio.VersionResolver
import me.modmuss50.mpp.ReleaseType
import net.fabricmc.loom.LoomGradlePlugin
import net.fabricmc.loom.LoomNoRemapGradlePlugin
import net.fabricmc.loom.api.LoomGradleExtensionAPI
import net.fabricmc.loom.task.RemapJarTask

plugins {
    kotlin("jvm") version "2.3.20"
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
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

val outputJarTask =
    if (noMappings) {
        tasks.named<Jar>("jar")
    } else {
        tasks.named<RemapJarTask>("remapJar")
    }

publishMods {
    changelog =
        rootProject.layout.projectDirectory
            .file("changelog.md")
            .asFile
            .readText()
    type = ReleaseType.BETA
    modLoaders.add("fabric")

    displayName = "[Fabric $minecraftVersion] ReplayModAudioRender ${rootProject.property("mod_version")}"
    file = outputJarTask.flatMap { it.archiveFile }

    val modrinthToken =
        providers
            .gradleProperty("modrinth_token")
            .orElse(providers.environmentVariable("MODRINTH_TOKEN"))
            .orNull
            ?.takeIf { it.isNotBlank() }

    dryRun = modrinthToken == null

    val minecraftVersions =
        VersionResolver
            .getMinecraftVersionsInRange("release", project.property("minecraft_version_dependency") as String)
            .get()
            .map { it.id }

    modrinth {
        projectId = "JNgb4oIM"
        accessToken = modrinthToken
        this.minecraftVersions.addAll(minecraftVersions)
    }
}
