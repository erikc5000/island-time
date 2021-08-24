plugins {
    `multiplatform-library`
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(libs.serializationCore)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinTest)
                implementation(libs.serializationJson)
            }
        }
    }
}
