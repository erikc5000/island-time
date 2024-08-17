plugins {
    `published-mpp-library`
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
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
                implementation(libs.javamath2kmp)
                compileOnly(libs.serializationCore)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinTest)
            }
        }

        val jvmMain by getting {
            resources.srcDirs("src/jvmMain/resources")
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.truth)
            }
        }

        val darwinMain by getting {
            dependencies {
                api(libs.serializationCore)
            }
        }
    }
}
