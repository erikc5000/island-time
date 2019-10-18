plugins {
    `android-library`
}

android {
    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/java", "src/test/kotlin")
    }
}

dependencies {
    implementation(project(":core"))
    implementation(kotlin("stdlib-jdk8"))

    api("com.jakewharton.threetenabp:threetenabp:1.2.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.google.truth:truth:1.0")
}