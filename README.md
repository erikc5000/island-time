[![Build Status](https://travis-ci.com/erikc5000/island-time.svg?branch=master)](https://travis-ci.com/erikc5000/island-time) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Island Time
A Kotlin Multiplatform library for working with dates and times, heavily inspired by the java.time library.

Features:
- A set of date-time primitives such as `Date`, `Time`, `DateTime`, `Instant`, and `ZonedDateTime`
- Time zone database support
- Date ranges and time intervals, integrating with Kotlin ranges and progressions
- Read and write strings in ISO formats
- DSL-based definition of custom parsers
- Operators like `date.next(MONDAY)` or `dateTime.startOfWeek`
- Works on JVM, Android, iOS, and macOS

Current limitations:
- No custom and/or localized format strings
- No localized week fields
- Only supports the ISO calendar system
- Year range currently restricted to 1-9999

# Setup
Island Time is still early in development and the API is likely to change significantly. Snapshot builds are available on the Sonatype OSS Snapshot Repository.

Repository configuration: _(Kotlin Gradle DSL)_
```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

This project publishes Gradle metadata, so you can use the common artifact and it will automatically sort out the correct dependencies for each platform.

Common: _(Kotlin Gradle DSL)_
```
dependencies {
    implementation("io.islandtime:core:0.1.0-SNAPSHOT")
}
```

On Android specifically, you'll need to add the following:

Android: _(Kotlin Gradle DSL)_
```
dependencies {
    // Until java.time library desugaring is added to D8, Android relies on
    // ThreeTenABP (https://github.com/JakeWharton/ThreeTenABP) to supply the
    // time zone database
    implementation("io.islandtime:threetenabp-extensions:0.1.0-SNAPSHOT")
}
```

_**Important:**_ Due to the experimental status of inline classes, which are used in the public API, the version of Kotlin that you use in your project must match the version used by Island Time -- even for non-native targets.

Island Time 0.1.0-SNAPSHOT builds are based on Kotlin 1.3.60.
Also note that Island Time requires a JVM target of 1.8 or above.

Current supported platforms are JVM, Android, iOS ARM64/x64, and macOS x64.

# Usage

## Initialization

Prior to using Island Time, it may be initialized with a custom `TimeZoneRulesProvider`. The platform default provider will be used if this isn't specified explicitly.

On Android, using the default provider will trigger an exception at runtime with any version below API 26-- unless you're using an Android Gradle Plugin 4.0 alpha build with `coreLibraryDesugaringEnabled = true`. Until AGP 4.0 and java.time desugaring becomes stable, it's recommended that you use the `AndroidThreeTenProvider` in `threetenabp-extensions` instead, which uses the Android ThreenTen backport under the hood to provide time zone data. Generally, initialization should be performed during `Application.onCreate()`.

```kotlin
// Note that it isn't necessary to call AndroidThreeTen.init() separately
IslandTime.initializeWith(AndroidThreeTenProvider(context))
```

For further information on the Android backport, see https://github.com/JakeWharton/ThreeTenABP.

## For those coming from java.time

I suspect this is many of you, so I'm putting this here first. As Island Time draws heavily from the java.time library design, many of the core classes and concepts should be familiar to anyone migrating over. The following table shows the relationship between a subset of the classes:

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

That said, Island Time is not a strict port. It takes inspiration from other date-time libraries as well with the goal being to (ultimately) create a powerful library that enables a wide array of use cases while providing a more friendly, extension-oriented API that takes full advantage of Kotlin language features.

Some notable differences from java.time:

##### `OffsetTime`, `OffsetDateTime`, and `ZonedDateTime` don't implement `Comparable`

In java.time, using the `<` or `>` to compare objects of any of these classes probably doesn't do what you'd expect. In the interest of being "consistent with equals", comparison is based on a natural ordering that looks at the instant first, but then other properties of these objects such as the time or date-time to provide a fully deterministic ordering in the face of differing offsets and regions. You need to use `isBefore()` or `isAfter()` to compare based solely on timeline order and failing to do so can lead to subtle bugs -- say if using them in a `ClosedRange`.

For that reason, Island Time doesn't implement `Comparable` for these classes, instead requiring you to be explicit about what order you want when it comes to sorting or use of sorted containers. The companion objects have `TIMELINE_ORDER` and `DEFAULT_SORT_ORDER` comparators available. Island Time does, however, provide `compareTo` operators that are based on timeline order for convenience. This may change since it still creates some "inconsistent with equals" issues.

##### No "temporal adjusters" -- for example

While java.time uses a highly generic mechanism that allows you to do things like `date.with(TemporalAdjusters.firstDayOfMonth())`, 
Island Time opts for a more "extension-oriented" approach, allowing you to do the same with `date.startOfMonth`. Certainly, you can build your own extension functions around an OO mechanism like that offered in java.time, but the core problem is that some of these mechanisms are overally abstract, providing opportunity for runtime failure, creating a higher learning curve, and offering poor out-of-the-box IDE discoverability.

While Island Time will need to get more "abstract" in some areas as more features are added, we want to create something that's a little more approachable and won't compile if you try to do nonsensical things -- even if it means increasing the method count significantly to do so.

##### You may not need to use `Duration` at all

Island Time provides inline classes for individual duration units, backed by either a `Long` or `Int` -- for example, `IntYears`, `IntHours`, or `LongNanoseconds`. This allows you to represent a duration with a specific level of precision. When adding or subtracting quantities in mixed units, precision is increased automatically as needed. For example:

```kotlin
val total: LongMilliseconds = 5.days + 5.hours - 500.milliseconds
```

Unless you're doing calculations with particularly long durations at a high precision where overflow is a very real possibility, you might not need to use `Duration` class at all. The ability to do this in an efficient manner is really enabled by inline classes, which just aren't an option for a Java library.

##### DSL-based parser definition

While Island Time doesn't yet support custom formats, it does support support custom parsers, which can be defined using a DSL rather than the traditional format strings.

```kotlin
val customParser = dateTimeParser {
    monthNumber() { enforceSignStyle(SignStyle.NEVER) }
    anyOf({ +'-' }, { +'/' })
    dayOfMonth() { enforceSignStyle(SignStyle.NEVER) }
    anyOf({ +'-' }, { +' ' })
    year(length = 4) { enforceSignStyle(SignStyle.NEVER) }
}
```

Or to parse more than one date-time, as with an interval:

```kotlin
val intervalParser = groupedDateTimeParser {
    group {
        childParser(DateTimeParsers.Iso.CALENDAR_DATE)
    }
    +"--"
    group {
        childParser(DateTimeParsers.Iso.CALENDAR_DATE)
    }
}

val dateTime = someString.toDateTimeInterval(intervalParser)
```

This is in contrast to the builder-based approach in java.time, which you probably never used since the API isn't so nice. Ultimately, support for format strings may be added in addition to the DSL-based approach, but I think it does offer better readability and IDE discoverability.

## Examples

The following examples demonstrate how to use some of the features present in Island Time.

### Durations

```kotlin
// The minimum necessary unit granularity is preserved when combining different units
val totalSeconds: IntSeconds = 5.hours + 30.minutes + 1.seconds

// Math with Int values on sub-second units forcees a lengthing to Long due to overflow potential
val nanoseconds: LongNanoseconds = 5.seconds + 1.nanoseconds

// The generalized "Duration" class is suitable for dealing with potentially large durations (ie. >50 years)
// that might overflow an Int or Long representation
val duration: Duration = durationOf(5.hours + 30.minutes - 5.milliseconds)
val anotherDuration: Duration = 5.hours.asDuration() + 50.microseconds

// Durations (and each unit measure) can be broken down into individual unit components
duration.toComponents { hours, minutes, seconds, nanoseconds ->
    // ...
}
```

### Periods

```kotlin
val period = periodOf(5.years, 13.months, 10.days)
val normalizedPeriod = period.normalized() // periodOf(6.years, 1.months, 10.days)
val modifiedPeriod = period - 1.years - 15.days // periodOf(5.years, 1.months, (-5).days)
val invertedPeriod = -period // periodOf((-5).years, (-13).months, (-10).days)
```

### Date Manipulation

```kotlin
val today = Date.now()
val tomorrow = today + 1.days
val nextWednesday = today.next(DayOfWeek.WEDNESDAY)
val lastSundayOrToday = today.previousOrSame(DayOfWeek.SUNDAY)
val startOfMonth = today.startOfMonth
val endOfMonth = today.endOfMonth
```

### ISO-8601 Representation

```kotlin
val isoTimestamp = Instant.now().toString() // 2019-10-28T08:34:03.389Z
val isoDuration = durationOf(5.hours + 4.seconds).toString() // PT5H4S
```

### Parsing

```kotlin
// Parses ISO-8601 extended format strings by default
val offsetDateTime = "2001-08-09T12:45+04:00".toOffsetDateTime()

// Built-in parsers are also available for basic and basic/extended formats
val dateTime = "20000101 0909".toDateTime(DateTimeParsers.Iso.Basic.DATE_TIME)

// Custom parsers can also be defined, but must supply a combination of DateTimeFields that the type can interpret
val customParser = dateTimeParser {
    monthNumber(2)
    anyOf({ +'-' }, { +' ' })
    dayOfMonth(2)
    anyOf({ +'-' }, { +' ' })
    year(4)
}

val date = "10-01-2019".toDate(customParser)
```

### Date Ranges

Note that in Island Time, "ranges" are inclusive, implementing Kotlin's `ClosedRange`, while intervals" are half-open with an exclusive end. When it comes to time, precision differences (ie. millisecond vs nanosecond) can make an inclusive end problematic.

```kotlin
val clock: Clock = SystemClock()
val today: Date = Date.now(clock)

// Step over each day in a range
for (date in today - 1.months..today) {
    val startOfDay: ZonedDateTime = date.startOfDayAt(clock.zone)
    val endOfDay: ZonedDateTime = date.endOfDayAt(clock.zone)
    // ...
}

// Step by months instead of days
for (date in today until today + 1.years step 1.months) {
   // ...
}

val randomDate = (today..today + 1.months).random()
val totalDays: IntDays = (today until today + 6.months).lengthInDays
val period: Period = (today..today + 1.months).asPeriod()
```

### Open Time Intervals

Island Time supports unbounded time intervals, using the `MIN` and `MAX` values for the date-time primitive to indicate "far past" or "far future". In the ISO standard, this is referred to as an "open" interval, but that conflicts with the mathematical definition of open/closed, so we've opted not to use that terminology (see `ClosedRange`).

```kotlin
val instantInterval = "2008-09-01T04:00Z/..".toInstantInterval()

val isBounded = instantInterval.isBounded() // false
val hasUnboundedEnd = instantInterval.hasUnboundedEnd() // true
val duration = instantInterval.asDuration() // throws DateTimeException
```

### Daylight Savings Changes

```kotlin
// Gaining an hour (ie. overlap)
val zonedDateTime = "2019-11-03T01:00-04:00[America/New_York]".toZonedDateTime()
val laterOffset = zonedDateTime.withLaterOffsetAtOverlap() // 2019-11-03T01:00-05:00[America/New_York]
val anHourLater = zonedDateTime + 1.hours // 2019-11-03T01:00-5:00[America/New_York]
val nextDay = zonedDateTime + 1.days // 2019-11-04T01:00-05:00[America/New_York] (1 day = 25 hours in this case)

// Losing an hour (ie. gap)
val zonedDateTime = Date(2019, MARCH, 11) at Time(1, 30) at TimeZone("America/New_York") // -5 hours UTC

val plus30Mins = zonedDateTime + 1.hours
// 2019-03-11T03:30-04:00[America/New_York] (Note that 2:30AM doesn't exist and is bumped to 3:30AM)

val nextDay = zonedDateTime + 1.days // 2019-03-11T01:30-04:00[America/New_York] (1 day = 23 hours in this case)
```

### Interop

A set of extensions are available that will allow you to convert to and from platform date-time primitives.

java.time / ThreeTenABP:
```kotlin
val javaLocalDate = Date(2019, OCTOBER, 24).toJavaLocalDate()
val islandDate = LocalDate.of(2019, OCTOBER, 24).toIslandDate()
```

iOS:
```kotlin
val nsDate = Instant.now().toNSDate()
val islandInstant = NSDate().toIslandInstant()
```

## Notes on kotlin.time

An [experimental time API](https://github.com/Kotlin/KEEP/issues/190) has recently been added to the Kotlin standard library. Unfortunately, its design does't agree well with Island Time -- at least currently.

The Kotlin `Duration` class is based on a floating point number, which we steer clear of to avoid any accuracy issues that might come about during manipulation of floating point values and offer a fixed nanosecond precision across the entire supported time range. We also opt to preserve unit granularity. For example, `1.seconds` translates to `IntSeconds` rather than `Duration`. This allows you to specify a particular unit granularity in your code when required.

Currently, Kotlin's `WallClock` isn't available and doesn't offer time zone support in any case, so we have our own `Clock` implementation as well. Island Time takes an extension-oriented approach to clocks, enabling support for multiple implementations -- in fact, it's in envisioned that there will be separate millisecond and nanosecond precision clocks. In the future, we can concievably offer support for any standard library clock implementation as well.

# Feedback/Contributions

The goal of this project is not just to port the java.time library over to Kotlin Multiplatform, but to take full advantage of Kotlin language features to create a date-time DSL that feels natural to users of the language and encourages best practices where possible. To that end, any and all feedback would be much appreciated in helping to iron out the API.

If you're interested in contributing or have ideas on areas that can be improved (there are definitely many right now), please feel free to initiate a dialog by opening design-related issues or submitting pull requests.
