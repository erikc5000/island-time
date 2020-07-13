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

Due to the experimental status of [inline classes](https://kotlinlang.org/docs/reference/inline-classes.html), which are used in Island Time's public API, the version of Kotlin that you use in your project must match the version used by Island Time -- even for non-native targets. Those of you who are using [Kotlin/Native](https://kotlinlang.org/docs/reference/native-overview.html) are probably already accustomed to dealing with this since there is no binary compatibility between releases yet.

| Island Time Version | Kotlin Version |
| --- | --- |
| 0.1.x | 1.3.6x |
| 0.2.x | 1.3.7x |

!!! info "Kotlin 1.4 preview builds"
    Island Time builds that are compatible with the latest Kotlin 1.4 preview build are also available starting with 1.4-M3. Just add the appropriate suffix to the version number. For example, `{{ versions.islandtime }}` would be `{{ versions.islandtime }}-1.4-M3`.

### JVM

Island Time requires Java 8 or above.

### Android

Island Time requires a minimum compile SDK of API 15 or later and Java 8 support should be [turned on](https://developer.android.com/studio/write/java8-support).

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

#### Android Studio 4.0 or later

Make sure that [core library desugaring](https://developer.android.com/studio/preview/features#j8-desugar) is enabled.

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
        coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:1.0.9'
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
            coreLibraryDesugaringEnabled = true
            // Sets Java compatibility to Java 8
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    dependencies {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.9")
    }
    ```

#### Android Studio 3.6 or earlier

You'll need to add an additional dependency on the ThreeTen Android Backport. You can find details [here](extensions/threetenabp.md).

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