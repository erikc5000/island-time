plugins {
    `multiplatform-library`
    alias(libs.plugins.serialization)
}

kotlin {
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(project(":core"))
                implementation(libs.serializationCore)
                implementation(libs.serializationJson)
                implementation(libs.kotlinTest)
            }
        }
    }
}
