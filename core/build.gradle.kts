plugins {
    id("islandtime-multiplatform")
    id("kotlinx-atomicfu")
}

kotlin {
    sourceSets {
        commonMain.get().kotlin.srcDirs("src/commonMain/generated")

        darwinMain.get().dependencies {
            implementation(Libs.AtomicFU.runtime)
        }
    }

    jvm {
        withJava()
        compilations["test"].defaultSourceSet.dependencies {
            implementation(Libs.googleTruth)
        }
    }
}

tasks.dokka.get().multiplatform.create("global") {
    perPackageOption {
        prefix = "io.islandtime.internal"
        suppress = true
    }

    perPackageOption {
        prefix = "io.islandtime.measures.internal"
        suppress = true
    }

    perPackageOption {
        prefix = "io.islandtime.parser.internal"
        suppress = true
    }

    perPackageOption {
        prefix = "io.islandtime.ranges.internal"
        suppress = true
    }

    perPackageOption {
        includes = listOf("packages.md")
    }
}
