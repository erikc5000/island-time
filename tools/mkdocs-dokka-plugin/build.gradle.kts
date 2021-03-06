import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
}

dependencies {
    compileOnly("org.jetbrains.dokka:dokka-core:1.4.20")
    implementation("org.jetbrains.dokka:dokka-base:1.4.20")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
