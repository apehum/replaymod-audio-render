plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.15.5" apply false
}
stonecutter active "1.19.3-fabric"

stonecutter parameters {
    constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge")
}

gradle.projectsEvaluated {
    subprojects.sortedBy { it.name }.zipWithNext { prev, curr ->
        curr.tasks.matching { it.name.startsWith("publish") }.configureEach {
            val taskName = name
            mustRunAfter(prev.tasks.matching { it.name == taskName })
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases")

        maven("https://api.modrinth.com/maven") {
            name = "Modrinth"
            content { includeGroup("maven.modrinth") }
        }

        maven("https://cursemaven.com") {
            name = "CurseMaven"
            content { includeGroup("curse.maven") }
        }

        maven("https://jitpack.io") {
            name = "JitPack"
            content { includeGroupByRegex("com\\.github\\..*") }
        }
    }
}
