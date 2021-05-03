plugins {
    `multiplatform-library`
    id("kotlinx-atomicfu")
}

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
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

        val jvmTest by getting {
            dependencies {
                implementation(Libs.googleTruth)
            }
        }
    }
}
