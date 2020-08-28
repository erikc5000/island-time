import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `multiplatform-library`
    kotlin("plugin.serialization") version Versions.kotlin
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(Libs.Serialization.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}
