# Getting Started

## Supported Platforms

As a [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) library, Island Time isn't restricted to just the JVM or Android. Currently, the following platforms are supported:

- JVM
- Android
- iOS ARM64/x64/Simulator ARM64
- macOS x64/ARM64
- watchOS ARM64/x64/Simulator ARM64
- tvOS ARM64/x64/Simulator ARM64

## Version Requirements

| Island Time Version | Kotlin Version | Notes                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|---------------------|----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 0.1.x               | 1.3.6x         |
| 0.2.x               | 1.3.7x         |
| 0.3.x/0.4.x         | 1.4.x          | Island Time 0.4.0 is built/published with Kotlin 1.4.20. While binary compatible with earlier versions, if you're using [HMPP](https://kotlinlang.org/docs/reference/mpp-share-on-platforms.html#share-code-on-similar-platforms) in your project, you'll need to update to at least 1.4.20 due to publishing changes. See the Kotlin 1.4.20 [release notes](https://blog.jetbrains.com/kotlin/2020/11/kotlin-1-4-20-released/) for details. |
| 0.5.x               | 1.4.3x         | Island Time 0.5.0 uses the [new inline class name mangling](https://kotlinlang.org/docs/whatsnew1430.html#improved-inline-classes) introduced in Kotlin 1.4.30, requiring that you use at least that version.                                                                                                                                                                                                                                |
| 0.6.x               | 1.5+           |

### JVM

Java 11 or above is required.

### Android

Android Gradle Plugin 7.4.0 or above and a minimum compile SDK of API 21 are required.

## Gradle Setup

### Common

Add the following dependency to your project's Gradle script:

=== "Kotlin"
    ```kotlin
    dependencies {
        implementation("io.islandtime:core:{{ versions.islandtime }}")
    }
    ```

=== "Groovy"
    ```groovy
    dependencies {
        implementation "io.islandtime:core:{{ versions.islandtime }}"
    }
    ```

Extensions are also available for the [`@Parcelize`](extensions/parcelize.md) feature on Android.

### Android

You'll need to turn on [core library desugaring](https://developer.android.com/studio/preview/features#j8-desugar) if it isn't enabled already.

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
            // Sets Java compatibility to Java 11
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    dependencies {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")
    }
    ```

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
            // Sets Java compatibility to Java 11
            sourceCompatibility JavaVersion.VERSION_11
            targetCompatibility JavaVersion.VERSION_11
        }
    }

    dependencies {
        coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.0.3'
    }
    ```

### Snapshot builds

Development snapshot builds are available in the Sonatype OSS Snapshot Repository. To use one, you'll need to add that repository to your project's Gradle script:

=== "Kotlin"
    ```kotlin
    repositories {
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
    ```

=== "Groovy"
    ```groovy
    repositories {
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    ```
