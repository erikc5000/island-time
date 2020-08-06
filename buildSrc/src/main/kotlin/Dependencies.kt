object Versions {
    const val kotlin = "1.4.0-rc"
    const val serialization = "1.0-M1-1.4.0-rc"
    const val atomicfu = "0.14.3-1.4.0-rc"
    const val androidxTest = "1.2.0"
    const val googleTruth = "1.0.1"
    const val kotlinpoet = "1.6.0"
    const val androidDesugarJdkLibs = "1.0.10"
}

object Libs {
    object Serialization {
        const val runtime = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.serialization}"
    }

    object AtomicFU {
        const val runtime = "org.jetbrains.kotlinx:atomicfu:${Versions.atomicfu}"
    }

    object AndroidxTest {
        const val runner = "androidx.test:runner:${Versions.androidxTest}"
        const val orchestrator = "androidx.test:orchestrator:${Versions.androidxTest}"
    }

    const val googleTruth = "com.google.truth:truth:${Versions.googleTruth}"
    const val kotlinpoet = "com.squareup:kotlinpoet:${Versions.kotlinpoet}"
    const val androidDesugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.androidDesugarJdkLibs}"
}
