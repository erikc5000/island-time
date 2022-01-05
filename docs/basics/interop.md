# Interoperating with Platform Libraries

In a multiplatform project, it's often necessary to interoperate with code that uses platform libraries directly. A number of conversion functions are available to help simplify this process.

## Java Time Library

Island Time's classes map very closely to those in [java.time](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/time/package-summary.html), making it pretty easy to go back and forth between them. The following table shows the relationship between a subset of the classes:

| java.time | Island Time | Description |
| --- | --- | --- |
| `LocalDate` | [`Date`](../api/core/core/io.islandtime/-date/index.md) | A date in arbitrary region |
| `LocalTime` | [`Time`](../api/core/core/io.islandtime/-time/index.md) | A time of day in arbitrary region |
| `LocalDateTime` | [`DateTime`](../api/core/core/io.islandtime/-date-time/index.md) | A combined date and time of day in arbitrary region |
| `Instant` | [`Instant`](../api/core/core/io.islandtime/-instant/index.md) | An instant in time, represented by the number of seconds/nanoseconds relative to the Unix epoch (`1970-01-01T00:00Z`) |
| `OffsetTime` | [`OffsetTime`](../api/core/core/io.islandtime/-offset-time/index.md) | A time of day with UTC offset |
| `OffsetDateTime` | [`OffsetDateTime`](../api/core/core/io.islandtime/-offset-date-time/index.md) | A date and time of day with fixed UTC offset |
| `ZonedDateTime` | [`ZonedDateTime`](../api/core/core/io.islandtime/-zoned-date-time/index.md) | A date and time of day in a particular time zone region |
| `ZoneOffset` | [`UtcOffset`](../api/core/core/io.islandtime/-utc-offset/index.md) | An offset from UTC |
| `ZoneId` | [`TimeZone`](../api/core/core/io.islandtime/-time-zone/index.md) | An IANA time zone database region ID or fixed offset from UTC |
| `Duration` | [`Duration`](../api/core/core/io.islandtime.measures/-duration/index.md) | A (potentially large) duration of time |
| `Period` | [`Period`](../api/core/core/io.islandtime.measures/-period/index.md) | A date-based period of time |

To convert between an Island Time `Date` and Java `LocalDate`, you can do something like this:

```kotlin
val javaLocalDate = java.time.LocalDate.now()

// Convert a Java LocalDate to Island Time Date
val islandDate = javaLocalDate.toIslandDate()

// Convert an Island Time Date back to a Java LocalDate
val backToJavaLocalDate = islandDate.toJavaLocalDate()
```

The pattern above can be applied for the majority of the date-time and duration classes. For durations, it's also possible to convert any of Island Time's single unit durations directly to a Java `Duration`.

```kotlin
val javaDuration: java.time.Duration = 30.minutes.toJavaDuration()
```

You can find the full set of conversions in the [io.islandtime.jvm](../api/core/core/io.islandtime.jvm/index.md) package.

## Apple Foundation Classes

We can map between some Island Time types and the date-time types provided in Apple's [Foundation API](https://developer.apple.com/documentation/foundation/dates_and_times?language=objc), such as `NSDate`, `NSDateComponents`, and `NSTimeZone`. Keep in mind that `NSDate` and `NSTimeInterval` are based around floating-point numbers, so conversion may result in lost precision.

```kotlin
// NSDate is a timestamp, just like Instant
val islandInstant = Instant.now()
val nsDate = islandInstant.toNSDate()
val islandInstantAgain = nsDate.toIslandInstant()

// NSDateComponents separates out the calendar/time components, roughly modeling
// Date, DateTime, ZonedDateTime, et all
val zonedDateTime = ZonedDateTime.now()
val nsDateComponents = zonedDateTime.toNSDateComponents()
val zonedDateTimeAgain = nsDateComponents.toIslandZonedDateTimeOrNull()

// Convert from IntMinutes to NSTimeInterval
val nsTimeInterval = 5.minutes.toNSTimeInterval()
```

The full set of conversions can be found in the [io.islandtime.darwin](../api/core/core/io.islandtime.darwin/index.md) package.

## kotlin.time

As of Kotlin 1.6.0, a [`Duration`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/) type has been stabilized as part of the standard library. It isn't an exact replacement for Island Time's `Duration`, but you can easily convert between the two types (with possible loss of precision for larger durations).

```kotlin
import kotlin.time.seconds as kotlinSeconds

// Convert from Island Time IntMinutes to Kotlin Duration
val kotlinDuration: kotlin.time.Duration = 30.minutes.toKotlinDuration()

// Convert from Kotlin Duration to Island Time Duration
val islandDuration: Duration = 30.kotlinSeconds.toIslandDuration()
```

Kotlin's `Duration` type can also be used directly with Island Time classes, such as `Instant` or `ZonedDateTime`.

```kotlin
val untilInstant = Instant.now() + 10.kotlinSeconds
```
