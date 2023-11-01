plugins {
    `published-android-library`
    id("kotlin-parcelize")
}

android {
    namespace = "io.islandtime.parcelize"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    defaultConfig {
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

dependencies {
    coreLibraryDesugaring(libs.androidDesugarJdkLibs)

    implementation(project(":core"))

    androidTestImplementation(libs.androidxTestRunner)
    androidTestImplementation(libs.truth)
    androidTestUtil(libs.androidxTestOrchestrator)
}
