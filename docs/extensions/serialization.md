# Serialization

Island Time provides an artifact containing custom serializers for use with [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization). These can be used to convert dates, times, durations, and intervals into ISO-compatible strings, which work well with JSON.

## Gradle Setup

Add the following dependency to your project's Gradle script:

=== "Groovy"
    ```groovy
    dependencies {
        implementation "io.islandtime:serialization-extensions:{{ versions.islandtime }}"
    }
    ```

=== "Kotlin"
    ```kotlin
    dependencies {
        implementation("io.islandtime:serialization-extensions:{{ versions.islandtime }}")
    }
    ```

!!! note
    Island Time publishes Gradle Module Metadata for all multiplatform artifacts. The listed "common" artifacts may be used in the dependency block of any target, common or platform-specific.

## Serializing to JSON

For example purposes, let's assume we have a data structure describing an event that we'd like to serialize.

```kotlin
@Serializable
data class EventDto(
    val name: String,
    @Serializable(with = DateRangeSerializer::class) val dateRange: DateRange,
    @Serializable(with = InstantSerializer::class) val createdAt: Instant
)
```

By using the `@Serializable` annotation, we instruct the Kotlin Serialization plugin to generate a serializer for the `EventDto` class. Island Time's [DateRange](../api/core/io.islandtime.ranges/-date-range/index.md) and [Instant](../api/core/io.islandtime/-instant/index.md) classes are not serializable out of the box, so we explicitly specify the serializers provided by Island Time.

Now, we can serialize the `EventDto` class to JSON with the following code:

```kotlin
fun writeToJson(val event: EventDto): String {
    val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
    return json.stringify(EventDto.serializer(), event)
}
```

Example output might look something like this:

```json
{
    "name": "KotlinConf 2019",
    "dateRange": "2019-12-04/2012-12-06",
    "createdAt": "2020-03-14T14:19:03.478Z"
}
```

For more information on how to use Kotlin Serialization, consult the [GitHub page](https://github.com/Kotlin/kotlinx.serialization).

## Binary Formats

At the present time, there are no serializers tuned specifically for binary formats. If you have a use case that requires that, feel free to raise an [issue](https://github.com/erikc5000/island-time/issues).
