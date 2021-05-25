plugins {
    `multiplatform-library`
    kotlin("plugin.serialization") version Versions.kotlin
}

kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(project(":core"))
                implementation(Libs.Serialization.core)
                implementation(Libs.Serialization.json)
                implementation(kotlin("test"))
            }
        }
    }
}
