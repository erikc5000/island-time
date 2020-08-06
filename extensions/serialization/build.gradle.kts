plugins {
    id("islandtime-multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets.commonMain.get().dependencies {
        implementation(project(":core"))
        implementation(Libs.Serialization.runtime)
    }
}
