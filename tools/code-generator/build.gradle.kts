import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.30"
    application
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.6.0")
}

application {
    mainClass.set("io.islandtime.codegen.MainKt")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
