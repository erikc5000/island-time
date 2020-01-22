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

    api("com.jakewharton.threetenabp:threetenabp:1.2.2")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.google.truth:truth:1.0")

    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("com.google.truth:truth:1.0")
    androidTestUtil("androidx.test:orchestrator:1.2.0")
}