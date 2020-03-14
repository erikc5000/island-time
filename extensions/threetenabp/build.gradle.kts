plugins {
    `android-library`
}

android {
    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/java", "src/test/kotlin")
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

    api(Libs.threetenabp)

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation(Libs.googleTruth)

    androidTestImplementation(Libs.AndroidxTest.runner)
    androidTestImplementation(Libs.googleTruth)
    androidTestUtil(Libs.AndroidxTest.orchestrator)
}