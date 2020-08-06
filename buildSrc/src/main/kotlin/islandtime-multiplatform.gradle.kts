plugins {
    kotlin("multiplatform")
    id("islandtime-published")
    jacoco
}

val ideaActive get() = System.getProperty("idea.active") == "true"

kotlin {
    jvm().compilations["test"].defaultSourceSet.dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
    }

    val darwinTargets = if (ideaActive) {
        listOf(iosX64("darwin"))
    } else {
        listOf(iosArm64(), iosX64(), macosX64(), watchosArm64(), watchosX86(), tvosArm64(), tvosX64())
    }

    sourceSets {
        commonTest.get().dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }

        if (!ideaActive) {
            val darwinMain by creating {
                dependsOn(commonMain.get())
            }

            val darwinTest by creating {
                dependsOn(commonTest.get())
            }

            configure(darwinTargets) {
                compilations["main"].defaultSourceSet.dependsOn(darwinMain)
                compilations["test"].defaultSourceSet.dependsOn(darwinTest)
            }
        }

        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.RequiresOptIn")
                progressiveMode = true
            }
        }
    }
}

publishing {
    val pomMppArtifactId: String? by project

    val emptySourcesJar by tasks.registering(Jar::class) {
        archiveClassifier.set("sources")
    }

    publications.withType<MavenPublication> {
        if (name == "kotlinMultiplatform") {
            if (pomMppArtifactId != null) {
                artifactId = pomMppArtifactId
            }
            artifact(emptySourcesJar.get())
        } else if (pomMppArtifactId != null) {
            artifactId = "${pomMppArtifactId}-$name"
        }
    }
}

afterEvaluate {
    tasks.dokka.get().multiplatform {
        kotlin.targets.matching { it.name != "metadata" }.forEach { create(it.name) }
    }

    tasks.withType<JacocoReport> {
        classDirectories.setFrom(
            fileTree("${buildDir}/classes/kotlin/jvm/") {
                exclude("**/*Test*.*")
            }
        )

        sourceDirectories.setFrom(kotlin.sourceSets["commonMain"].kotlin.sourceDirectories)
        executionData.setFrom("${buildDir}/jacoco/jvmTest.exec")
    }
}
