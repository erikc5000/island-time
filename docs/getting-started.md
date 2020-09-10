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

!!! warning "Important"
    Your project's Kotlin compiler version must match the version used by Island Time.

Due to the experimental status of [inline classes](https://kotlinlang.org/docs/reference/inline-classes.html), which are used in Island Time's public API, the version of Kotlin that you use in your project must match the version used by Island Time &mdash; even for non-native targets.

| Island Time Version | Kotlin Version |
| --- | --- |
| 0.1.x | 1.3.6x |
| 0.2.x | 1.3.7x |
| 0.3.x | 1.4.0/1.4.10 |

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
