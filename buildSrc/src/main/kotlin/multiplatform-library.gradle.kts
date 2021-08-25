plugins {
    kotlin("multiplatform")
    id("published-library")
    jacoco
}

kotlin {
    jvm()

    val darwinTargets = listOf(
        iosArm64(),
        iosX64(),
        macosX64(),
        watchosArm64(),
        watchosX86(),
        watchosX64(),
        tvosArm64(),
        tvosX64()
    )

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                progressiveMode = true
            }
        }

        val commonMain by getting
        val commonTest by getting

        val darwinMain by creating {
            dependsOn(commonMain)
        }

        val darwinTest by creating {
            dependsOn(commonTest)
        }

        configure(darwinTargets) {
            compilations["main"].defaultSourceSet.dependsOn(darwinMain)
            compilations["test"].defaultSourceSet.dependsOn(darwinTest)
        }
    }
}

publishing {
    val pomMppArtifactId: String? by project

    publications.withType<MavenPublication>().configureEach {
        if (pomMppArtifactId != null) {
            artifactId = if (name == "kotlinMultiplatform") {
                pomMppArtifactId
            } else {
                "${pomMppArtifactId}-$name"
            }
        }
    }
}

afterEvaluate {
    tasks.withType<JacocoReport>().configureEach {
        classDirectories.setFrom(
            fileTree("${buildDir}/classes/kotlin/jvm/") { exclude("**/*Test*.*") }
        )

        sourceDirectories.setFrom(
            listOf("commonMain", "jvmMain").flatMap { kotlin.sourceSets[it].kotlin.sourceDirectories }
        )

        executionData.setFrom("${buildDir}/jacoco/jvmTest.exec")
    }
}
