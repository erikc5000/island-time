import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

kotlin {
    jvm()
//    js {
//        useCommonJs()
//        browser()
//        nodejs()
//    }
    iosArm64()
    iosX64()

    sourceSets {
        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.Experimental")
                progressiveMode = true
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
//        val jsMain by getting {
//            dependencies {
//                implementation(kotlin("stdlib-js"))
//            }
//        }
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
        val iosMain by creating {
            dependsOn(commonMain)
        }
    }

    configure(listOf(iosArm64(), iosX64())) {
        compilations.getByName("main") {
            source(sourceSets.getByName("iosMain"))
            extraOpts.add("-Xobjc-generics")
        }

        binaries.framework {
            baseName = "IslandTimeKotlin"
        }
    }
}

if (HostManager.hostIsMac) {
    tasks.register("iosTest") {
        val device = project.findProperty("iosDevice")?.toString() ?: "iPhone Xʀ"
        dependsOn(tasks.named("linkDebugTestIosX64"))
        group = JavaBasePlugin.VERIFICATION_GROUP
        description = "Run tests for target 'ios' on an iOS simulator"

        doLast {
            val binary = (kotlin.targets["iosX64"] as KotlinNativeTarget).binaries.getTest("DEBUG").outputFile

            exec {
                commandLine("xcrun", "simctl", "spawn", device, binary.absolutePath)
            }
        }
    }

    tasks.named("check") {
        dependsOn(tasks.named("iosTest"))
    }
}