pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
    centralScript = "build.gradle.kts"

    create(rootProject) {
        versions("1.21.1", "26.1.1")
        vcsVersion = "1.21.1"
    }
}

rootProject.name = "replaymodaudiorender"
