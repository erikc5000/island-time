#`@Parcelize`

The `parcelize-extensions` artifact provides a set of parcelers for use with the [Parcelable implementation generator](https://kotlinlang.org/docs/reference/compiler-plugins.html#parcelable-implementations-generator) plugin.

## Gradle Setup

=== "Kotlin"
    ```kotlin
    dependencies {
        implementation("io.islandtime:parcelize-extensions:{{ versions.islandtime }}")
    }
    ```

=== "Groovy"
    ```groovy
    dependencies {
        implementation "io.islandtime:parcelize-extensions:{{ versions.islandtime }}"
    }
    ```

## Usage

Custom parcelers are available for each of Island Time's date-time primitives, durations, and intervals, allowing you to use them within `Parcelable` classes.

```kotlin
@Parcelize
@TypeParceler<Date, DateParceler>()
data class MyParcelable(
    val name: String,
    val date: Date
) : Parcelable
```

In the above example, [DateParceler](../api/extensions/parcelize/parcelize-extensions/io.islandtime.parcelize/-date-parceler/index.md) is used to generate a class containing a non-nullable `Date`. You could make the `Date` nullable instead by using [NullableDateParceler](../api/extensions/parcelize/parcelize-extensions/io.islandtime.parcelize/-nullable-date-parceler/index.md).

```kotlin
@Parcelize
@TypeParceler<Date?, NullableDateParceler>()
data class MyParcelableWithNull(
    val name: String,
    val date: Date?
) : Parcelable
```

See the [Parcelize Extensions API documention](../api/extensions/parcelize/index.md) for the full list of available parcelers.
