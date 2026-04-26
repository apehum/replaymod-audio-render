pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    centralScript = "build.gradle.kts"

    create(rootProject) {
        versions("1.14.4", "1.18.2", "1.19.3", "26.1.1")
        vcsVersion = "1.19.3"
    }
}

rootProject.name = "replaymodaudiorender"
