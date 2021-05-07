plugins {
    `multiplatform-library`
    id("kotlinx-atomicfu")
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        all {
            resources.setSrcDirs(emptyList<String>())
        }

        val commonMain by getting {
            kotlin.srcDirs("src/commonMain/generated")

            dependencies {
                implementation(Libs.javamath2kmp)
                implementation(Libs.atomicfu)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            resources.srcDirs("src/jvmMain/resources")
        }

        val jvmTest by getting {
            dependencies {
                implementation(Libs.googleTruth)
            }
        }
    }
}
