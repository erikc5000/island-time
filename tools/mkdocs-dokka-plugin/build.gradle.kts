import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
}

dependencies {
    compileOnly("org.jetbrains.dokka:dokka-core:1.4.32")
    implementation("org.jetbrains.dokka:dokka-base:1.4.32")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.4"
        languageVersion = "1.4"
    }
}
