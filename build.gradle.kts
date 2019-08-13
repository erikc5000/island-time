import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.3.41" apply false
}

buildscript {
    repositories {
        mavenCentral()
    }
}

allprojects {
    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
            jvmTarget = "1.8"
        }
    }

    group = "dev.erikchristensen.islandtime"
    version = "0.0.1"
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}