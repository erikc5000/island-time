plugins {
    kotlin("multiplatform")
    id("base-convention")
}

kotlin {
    jvm()

    val darwinTargets = listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
        watchosArm64(),
        watchosSimulatorArm64(),
        watchosX86(),
        watchosX64(),
        tvosArm64(),
        tvosX64(),
        tvosSimulatorArm64()
    )

    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                progressiveMode = true
            }
        }

        val commonMain by getting
        val commonTest by getting

        val darwinMain by creating {
            dependsOn(commonMain)
        }

        val darwinTest by creating {
            dependsOn(commonTest)
        }

        configure(darwinTargets) {
            compilations["main"].defaultSourceSet.dependsOn(darwinMain)
            compilations["test"].defaultSourceSet.dependsOn(darwinTest)
        }
    }
}
