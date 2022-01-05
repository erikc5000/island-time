plugins {
    `published-android-library`
    id("kotlin-parcelize")
}

android {
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
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
