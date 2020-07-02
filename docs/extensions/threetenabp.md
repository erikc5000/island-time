# ThreeTen Android Backport

Island Time normally depends on the java.time API to access time zone database information, but this is only available in Android API level 26 and above. While Android Studio 4.0's core library desugaring will alleviate this problem soon, in the interim, the `threetenabp-extensions` artifact provides support for using the [Android JSR-310 Backport](https://github.com/JakeWharton/ThreeTenABP) instead.

## Gradle Setup

Add the following Android-only dependency to your project's Gradle script:

=== "Groovy"
    ```groovy
    dependencies {
        implementation "io.islandtime:threetenabp-extensions:{{ versions.islandtime }}"
    }
    ```

=== "Kotlin"
    ```kotlin
    dependencies {
        implementation("io.islandtime:threetenabp-extensions:{{ versions.islandtime }}")
    }
    ```

## Initialization

You need to initialize Island Time with the [AndroidThreeTenProvider](../api/threetenabp/io.islandtime.extensions.threetenabp/-android-three-ten-provider/index.md) before using any of the library's functionality. Generally, you'll want to do this during `Application.onCreate()`.

```kotlin
// Note that it isn't necessary to call AndroidThreeTen.init() separately
IslandTime.initialize {
    timeZoneRulesProvider = AndroidThreeTenProvider(context)
}
```

Island Time can only be initialized once. Subsequent attempts to initialize it will throw an exception. This is intended to alert you to potentially undesirable behavior as a result of late initialization or attempts to use different providers in different places. In a test environment though, this can sometimes be problematic, so you may explciitly restore Island Time to an unitialized state using the `reset()` function:

```kotlin
IslandTime.reset()
// It's now safe to initialize Island Time again
```

For more information, see [Custom Providers](../advanced/custom-providers.md).

## Additional Extensions

In addition to the time zone database provider, there is also a set of extension methods allowing you to convert between Island Time types and equivalent Java types in the `org.threeten.bp` namespace. Consult the [API docs](../api/threetenabp/io.islandtime.extensions.threetenabp/index.md) for more details.