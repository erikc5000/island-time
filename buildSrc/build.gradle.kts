import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    `kotlin-dsl`
}

repositories {
    jcenter()
    google()
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/dokka/dev")
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.0-dev-53")
    implementation("com.android.tools.build:gradle:4.0.1")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
