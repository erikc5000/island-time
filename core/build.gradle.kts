import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `multiplatform-library`
}

apply(plugin = "kotlinx-atomicfu")

kotlin {
    jvm {
        withJava()
    }

    sourceSets {
        val commonMain by getting {
            kotlin.srcDirs("src/commonMain/generated")

            dependencies {
                implementation(Libs.javamath2kmp)
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
                implementation(Libs.googleTruth)
            }
        }
    }
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            includes.from(file("MODULE.md"))
        }
    }
}
