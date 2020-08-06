import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.serialization") version Versions.kotlin
    id("kotlinx-atomicfu") version Versions.atomicfu
}

subprojects {
    repositories {
        jcenter()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
            jvmTarget = "1.8"
        }
    }

    tasks.withType<AbstractTestTask> {
        testLogging {
            events = setOf(TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.FULL
            showStackTraces = true
        }
    }
}
