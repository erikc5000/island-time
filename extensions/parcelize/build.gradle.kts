plugins {
    `android-library`
    id("kotlin-android-extensions")
}

android {
    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    }

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))

    androidTestImplementation(project(":extensions:threetenabp"))
    androidTestImplementation(Libs.AndroidxTest.runner)
    androidTestImplementation(Libs.googleTruth)
    androidTestUtil(Libs.AndroidxTest.orchestrator)
}