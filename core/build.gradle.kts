import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
    `maven-publish`
    signing
}

kotlin {
    jvm {
        withJava()
    }
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
                progressiveMode = true
            }
        }

        val commonMain by getting {
            kotlin.srcDirs("src/commonMain/generated", "src/commonMain/kotlin")

            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("co.touchlab:stately:0.9.3")
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
                implementation("com.google.truth:truth:1.0")
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

            dependencies {
                implementation("co.touchlab:stately-collections:0.9.3")
            }
        }
    }

    configure(listOf(iosArm64(), iosX64())) {
        compilations.getByName("main") {
            source(sourceSets.getByName("iosMain"))
            extraOpts.add("-Xobjc-generics")
        }

        binaries.framework {
            baseName = "IslandTimeCore"
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