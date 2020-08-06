plugins {
    id("islandtime-android")
    id("kotlin-android-extensions")
}

android {
    compileOptions.coreLibraryDesugaringEnabled = true

    sourceSets["main"].java.srcDirs("src/main/kotlin")
    sourceSets["androidTest"].java.srcDirs("src/androidTest/kotlin")

    defaultConfig {
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions.execution = "ANDROIDX_TEST_ORCHESTRATOR"
}

dependencies {
    implementation(project(":core"))

    coreLibraryDesugaring(Libs.androidDesugarJdkLibs)

    androidTestImplementation(Libs.AndroidxTest.runner)
    androidTestImplementation(Libs.googleTruth)
    androidTestUtil(Libs.AndroidxTest.orchestrator)
}
