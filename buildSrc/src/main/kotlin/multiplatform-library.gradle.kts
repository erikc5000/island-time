import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform")
    id("published-library")
}

val ideaActive get() = System.getProperty("idea.active") == "true"

kotlin {
    jvm()

//    js {
//        useCommonJs()
//        browser()
//        nodejs()
//    }

    val iosTargets = if (ideaActive) {
        listOf(iosX64("ios"))
    } else {
        listOf(iosArm64(), iosX64(), macosX64())
    }

    sourceSets {
        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                progressiveMode = true
            }
        }

        if (!ideaActive) {
            val commonMain by getting
            val commonTest by getting

            @Suppress("UNUSED_VARIABLE")
            val iosMain by creating {
                dependsOn(commonMain)
            }

            @Suppress("UNUSED_VARIABLE")
            val iosTest by creating {
                dependsOn(commonTest)
            }
        }
    }

    configure(iosTargets) {
        compilations.getByName("main") {
            if (!ideaActive) defaultSourceSet.dependsOn(sourceSets["iosMain"])
            extraOpts.add("-Xobjc-generics")
        }

        if (!ideaActive) {
            compilations["test"].defaultSourceSet.dependsOn(sourceSets["iosTest"])
        }
    }
}

if (HostManager.hostIsMac) {
    registerIosTestTask(if (ideaActive) "ios" else "iosX64")
}

tasks.withType<DokkaTask> {
    doFirst {
        multiplatform {
            create("global") {
                perPackageOption {
                    prefix = "io.islandtime.internal"
                    suppress = true
                }
            }

            kotlin.targets.matching { it.name != "metadata" }.forEach { create(it.name) }
        }
    }
}

val emptySourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
}

afterEvaluate {
    publishing {
        publications.withType<MavenPublication>().named("kotlinMultiplatform") {
            artifact(emptySourcesJar.get())
        }
    }
}

fun registerIosTestTask(iosTargetName: String) {
    val iosTest by tasks.registering(RunIosTestsTask::class) {
        val kotlinNativeTarget = kotlin.targets[iosTargetName] as KotlinNativeTarget
        val testBinariesTaskName = kotlinNativeTarget.compilations[SourceSet.TEST_SOURCE_SET_NAME].binariesTaskName
        dependsOn(tasks.named(testBinariesTaskName))
        binary = kotlinNativeTarget.binaries.getTest(NativeBuildType.DEBUG).outputFile
    }

    tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
        dependsOn(iosTest)
    }
}