import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
    id("kotlinx-atomicfu") version "0.14.4" apply false
}

subprojects {
    tasks.withType<DokkaTask>().configureEach {
        dokkaSourceSets {
            configureEach {
                val pomArtifactId: String? by project
                val pomMppArtifactId: String? by project
                (pomArtifactId ?: pomMppArtifactId)?.let { moduleName.set(it) }

                includes.from(project.file("MODULE.md"))
                skipEmptyPackages.set(true)
                skipDeprecated.set(true)
            }
        }
    }
}

tasks.register("codegen") {
    dependsOn(gradle.includedBuild("code-generator").task(":run"))
}
