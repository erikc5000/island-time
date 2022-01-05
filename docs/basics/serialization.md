# Serialization

Island Time includes built-in support for [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization). By default, dates, times, durations, and intervals are serialized as ISO-compatible strings, which work well with JSON.

## Serializing to JSON

For example purposes, let's assume we have a data structure describing an event that we'd like to serialize.

```kotlin
@Serializable
data class EventDto(
    val name: String,
    val dateRange: DateRange,
    val createdAt: Instant
)
```

By using the `@Serializable` annotation, we instruct the Kotlin Serialization plugin to generate a serializer for the `EventDto` class. Island Time's [DateRange](../api/core/core/io.islandtime.ranges/-date-range/index.md) and [Instant](../api/core/core/io.islandtime/-instant/index.md) classes will be automatically serialized as ISO-8601 strings.

Now, we can serialize the `EventDto` class to JSON with the following code:

```kotlin
fun writeToJson(val event: EventDto): String {
    val json = Json { prettyPrint = true }
    return json.encodeToString(EventDto.serializer(), event)
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
