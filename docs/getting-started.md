# Getting Started

## Supported Platforms

As a [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) library, Island Time isn't restricted to just the JVM or Android. Currently, the following platforms are supported:

- JVM
- Android
- iOS ARM64/x64
- macOS x64
- watchOS ARM64/x86
- tvOS ARM64/x64

## Version Requirements

| Island Time Version | Kotlin Version | Notes |
| --- | --- | --- |
| 0.1.x | 1.3.6x |
| 0.2.x | 1.3.7x |
| 0.3.x/0.4.x | 1.4.x | Island Time 0.4.0 is built/published with Kotlin 1.4.20. While binary compatible with earlier versions, if you're using [HMPP](https://kotlinlang.org/docs/reference/mpp-share-on-platforms.html#share-code-on-similar-platforms) in your project, you'll need to update to at least 1.4.20 due to publishing changes. See the Kotlin 1.4.20 [release notes](https://blog.jetbrains.com/kotlin/2020/11/kotlin-1-4-20-released/) for details. |
| 0.5.x | 1.4.3x | Island Time 0.5.0 uses the [new inline class name mangling](https://kotlinlang.org/docs/whatsnew1430.html#improved-inline-classes) introduced in Kotlin 1.4.30, requiring that you use at least that version. |

### JVM

Java 8 or above is required.

### Android

Android Gradle Plugin 4.0 or above and a minimum compile SDK of API 21 are required.

## Gradle Setup

### Common

Add the following dependency to your project's Gradle script:

=== "Groovy"
    ```groovy
    dependencies {
        implementation "io.islandtime:core:{{ versions.islandtime }}"
    }
    ```

=== "Kotlin"
    ```kotlin
    dependencies {
        implementation("io.islandtime:core:{{ versions.islandtime }}")
    }
    ```

!!! note
    Island Time publishes Gradle Module Metadata for all multiplatform artifacts. The listed "common" artifacts may be used in the dependency block of any target, common or platform-specific.

Extensions are also available for [serialization](extensions/serialization.md) and the [`@Parcelize`](extensions/parcelize.md) feature on Android.

### Android

You'll need to turn on [core library desugaring](https://developer.android.com/studio/preview/features#j8-desugar) if it isn't enabled already.

=== "Groovy"
    ```groovy
    android {
        defaultConfig {
            // Required when setting minSdkVersion to 20 or lower
            multiDexEnabled true
        }

        compileOptions {
            // Flag to enable support for the new language APIs
            coreLibraryDesugaringEnabled true
            // Sets Java compatibility to Java 8
            sourceCompatibility JavaVersion.VERSION_1_8
            targetCompatibility JavaVersion.VERSION_1_8
        }
    }

    dependencies {
        coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.0.10'
    }
    ```

=== "Kotlin"
    ```kotlin
    android {
        defaultConfig {
            // Required when setting minSdkVersion to 20 or lower
            multiDexEnabled = true
        }

        compileOptions {
            // Flag to enable support for the new language APIs
            isCoreLibraryDesugaringEnabled = true
            // Sets Java compatibility to Java 8
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    dependencies {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")
    }
    ```

### Snapshot builds

Development snapshot builds are available in the Sonatype OSS Snapshot Repository. To use one, you'll need to add that repository to your project's Gradle script:

=== "Groovy"
    ```groovy
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    ```

=== "Kotlin"
    ```kotlin
    repositories {
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
    ```
