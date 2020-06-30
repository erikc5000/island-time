object Versions {
    const val kotlin = "1.4-M2"
    const val serialization = "0.20.0-1.4-M2"
    const val atomicfu = "0.14.3-1.4-M2"
    const val androidxTest = "1.2.0"
    const val googleTruth = "1.0.1"
    const val threetenabp = "1.2.4"
    const val stately = "1.0.4-a1-1.4-M2"
    const val kotlinpoet = "1.6.0"
    const val androidDesugarJdkLibs = "1.0.9"
}

object Libs {
    object Serialization {
        const val runtime = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.serialization}"
    }

    object AtomicFU {
        const val gradlePlugin = "org.jetbrains.kotlinx:atomicfu-gradle-plugin:${Versions.atomicfu}"
    }

    object AndroidxTest {
        const val runner = "androidx.test:runner:${Versions.androidxTest}"
        const val orchestrator = "androidx.test:orchestrator:${Versions.androidxTest}"
    }

    const val googleTruth = "com.google.truth:truth:${Versions.googleTruth}"
    const val threetenabp = "com.jakewharton.threetenabp:threetenabp:${Versions.threetenabp}"
    const val statelyIsolate = "co.touchlab:stately-isolate:${Versions.stately}"
    const val kotlinpoet = "com.squareup:kotlinpoet:${Versions.kotlinpoet}"
    const val androidDesugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.androidDesugarJdkLibs}"
}