object Versions {
    const val kotlin = "1.4.30"
    const val serialization = "1.1.0"
    const val atomicfu = "0.14.4"
    const val androidxTest = "1.3.0"
    const val googleTruth = "1.1"
    const val androidDesugarJdkLibs = "1.1.1"
    const val javamath2kmp = "0.3.0"
}

object Libs {
    object Serialization {
        const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.serialization}"
        const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"
    }

    object AndroidxTest {
        const val runner = "androidx.test:runner:${Versions.androidxTest}"
        const val orchestrator = "androidx.test:orchestrator:${Versions.androidxTest}"
    }

    const val googleTruth = "com.google.truth:truth:${Versions.googleTruth}"
    const val androidDesugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.androidDesugarJdkLibs}"
    const val javamath2kmp = "dev.erikchristensen.javamath2kmp:javamath2kmp:${Versions.javamath2kmp}"
}
