object Versions {
    const val kotlin = "1.5.20"
    const val serialization = "1.2.1"
    const val atomicfu = "0.16.2"
    const val androidxTest = "1.3.0"
    const val googleTruth = "1.1.3"
    const val androidDesugarJdkLibs = "1.1.1"
    const val javamath2kmp = "0.4.0"
}

object Libs {
    object Serialization {
        const val core = "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.serialization}"
        const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"
    }

    const val atomicfu = "org.jetbrains.kotlinx:atomicfu:${Versions.atomicfu}"

    object AndroidxTest {
        const val runner = "androidx.test:runner:${Versions.androidxTest}"
        const val orchestrator = "androidx.test:orchestrator:${Versions.androidxTest}"
    }

    const val googleTruth = "com.google.truth:truth:${Versions.googleTruth}"
    const val androidDesugarJdkLibs = "com.android.tools:desugar_jdk_libs:${Versions.androidDesugarJdkLibs}"
    const val javamath2kmp = "dev.erikchristensen.javamath2kmp:javamath2kmp:${Versions.javamath2kmp}"
}
