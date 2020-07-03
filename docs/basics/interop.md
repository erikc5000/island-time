# Interop

In a multiplatform project, it's often necessary to interoperate with code that uses platform libraries directly. A number of conversion functions are available to help simplify this process.

## Java Time Library

Island Time's classes map very closely to those in [java.time](https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/time/package-summary.html), making it pretty easy to go back and forth between them. The following table shows the relationship between a subset of the classes:

| java.time | Island Time | Description |
| --- | --- | --- |
| `LocalDate` | `Date` | A date in arbitrary region |
| `LocalTime` | `Time` | A time of day in arbitrary region |
| `LocalDateTime` | `DateTime` | A combined date and time of day in arbitrary region |
| `Instant` | `Instant` | An instant in time, represented by the number of seconds/nanoseconds relative to the Unix epoch (1970-01-01T00:00Z) |
| `OffsetTime` | `OffsetTime` | A time of day with UTC offset |
| `OffsetDateTime` | `OffsetDateTime` | A date and time of day with fixed UTC offset |
| `ZonedDateTime` | `ZonedDateTime` | A date and time of day in a particular time zone region |
| `ZoneOffset` | `UtcOffset` | An offset from UTC |
| `ZoneId` | `TimeZone` | An IANA time zone database region ID or fixed offset from UTC |
| `Duration` | `Duration` | A (potentially large) duration of time |
| `Period` | `Period` | A date-based period of time |

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

You can find the full set of conversions in the [io.islandtime.jvm](../api/core/io.islandtime.jvm/index.md) package.

## Apple Foundation Classes

We can map between some of the Island Time types and the date-time types provided in Apple's [Foundation API](https://developer.apple.com/documentation/foundation/dates_and_times?language=objc), such as `NSDate`, `NSDateComponents`, and `NSTimeZone`. Keep in mind that `NSDate` and `NSTimeInterval` are based around floating-point numbers, so conversion can result in lost precision sometimes.

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

The full set of conversions can be found in the [io.islandtime.darwin](../api/core/io.islandtime.darwin/index.md) package.

## kotlin.time

The Kotlin Standard Library has an experimental [`Duration`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/) type of its own, which you can convert to and from Island Time durations. Kotlin's `Duration` is based on a floating-point number, so keep in mind that conversion can be lossy.

```kotlin
import kotlin.time.seconds as kotlinSeconds

// Convert from Island Time IntMinutes to Kotlin Duration
val kotlinDuration: kotlin.time.Duration = 30.minutes.toKotlinDuration()

// Convert from Kotlin Duration to Island Time Duration
val islandDuration: Duration = 30.kotlinSeconds.toIslandDuration() 
```