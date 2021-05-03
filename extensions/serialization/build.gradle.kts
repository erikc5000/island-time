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
                implementation(kotlin("test"))
                implementation(Libs.Serialization.json)
            }
        }
    }
}
