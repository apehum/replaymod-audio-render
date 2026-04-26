plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.19.3"

gradle.projectsEvaluated {
    subprojects.sortedBy { it.name }.zipWithNext { prev, curr ->
        curr.tasks.matching { it.name.startsWith("publish") }.configureEach {
            val taskName = name
            mustRunAfter(prev.tasks.matching { it.name == taskName })
        }
    }
}
