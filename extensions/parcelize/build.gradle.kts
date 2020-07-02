plugins {
    `android-library`
    id("kotlin-android-extensions")
}

android {
    compileOptions {
        coreLibraryDesugaringEnabled = true
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
    coreLibraryDesugaring(Libs.androidDesugarJdkLibs)

    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))

    androidTestImplementation(Libs.AndroidxTest.runner)
    androidTestImplementation(Libs.googleTruth)
    androidTestUtil(Libs.AndroidxTest.orchestrator)
}