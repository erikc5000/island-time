# Interop

In a Kotlin Multiplatform project, it's often necessary to interoperate with code using platform libraries directly. Island Time provides a number of conversion operators to simplify this.

## Java Time Library

Island Time's classes map very closely to those in `java.time`, making it pretty easy to go back and forth between them.

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

## Apple Foundation Classes

We can map between some of the Island Time types and the date-time types provided in Apple's Foundation API, such as `NSDate`, `NSDateComponents`, and `NSTimeZone`. Keep in mind that `NSDate` and `NSTimeInterval` are based around floating-point numbers, so conversion can result in lost precision sometimes.

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

## `kotlin.time`

The Kotlin Standard Library has an experimental `Duration` type of its own, which you can convert to and from Island Time durations. Kotlin's `Duration` is based on a floating-point number, so keep in mind that conversion can be lossy.

```kotlin
import kotlin.time.seconds as kotlinSeconds

// Convert from Island Time IntMinutes to Kotlin Duration
val kotlinDuration: kotlin.time.Duration = 30.minutes.toKotlinDuration()

// Convert from Kotlin Duration to Island Time Duration
val islandDuration: Duration = 30.kotlinSeconds.toIslandDuration() 
```