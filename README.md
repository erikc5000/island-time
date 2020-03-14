![Build Status](https://github.com/erikc5000/island-time/workflows/Publish/badge.svg) [![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.islandtime/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.islandtime/core)

# Island Time
A Kotlin Multiplatform library for working with dates and times, heavily inspired by the java.time library.

Features:
- A full set of date-time primitives such as `Date`, `Time`, `DateTime`, `Instant`, and `ZonedDateTime`
- Time zone database support
- Date ranges and time intervals, integrating with Kotlin ranges and progressions
- Read and write strings in ISO formats
- DSL-based definition of custom parsers
- Access to localized text for names of months, days of the week, time zones, etc.
- Operators like `date.next(MONDAY)`, `dateTime.startOfWeek`, or `date.weekRange(WeekSettings.systemDefault())`
- Conversion to and from platform-specific date-time types
- Works on JVM, Android, iOS, macOS, tvOS, and watchOS

Current limitations:
- No custom format strings (must write platform-specific code to do this)
- Doesn't support all week-related fields or week-based dates
- Only supports the ISO calendar system

Island Time is still early in development and "moving fast" so to speak. The API is likely to experience changes between minor version increments.

# Setup

This project publishes Gradle metadata, so you can use the common artifact and it will automatically sort out the correct dependencies for each platform.

Common: _(Kotlin Gradle DSL)_
```
dependencies {
    implementation("io.islandtime:core:0.2.0")

    // Optional: A set of serializers for use with kotlinx.serialization
    implementation("io.islandtime:serialization-extensions:0.2.0")
}
```

On Android specifically, if you're using a version of Android Studio prior to 4.0, you'll need to add a dependency on the Android JSR-310 backport to provide Island Time with the required time zone database (more on this in [Initialization](#initialization)).

Android: _(Kotlin Gradle DSL)_
```
dependencies {
    // The following is only necessary if using a version of Android Studio
    // prior to 4.0 or if core library desugaring is turned off.
    implementation("io.islandtime:threetenabp-extensions:0.2.0")

    // Optional: A set of parcelers for use with the @Parcelize feature provided
    // by the Kotlin Android Extensions
    implementation("io.islandtime:parcelize-extensions:0.2.0")
}
```

_**Important:**_ Due to the experimental status of inline classes, which are used in the public API, the version of Kotlin that you use in your project must match the version used by Island Time -- even for non-native targets.

Island Time 0.2.x builds are based on Kotlin 1.3.70. Also note that Island Time requires a JVM target of 1.8 or above.

Current supported platforms are JVM, Android, iOS ARM64/x64, macOS x64, watchOS x64, and tvOS x86.

Snapshot builds are available on the Sonatype OSS Snapshot Repository (https://oss.sonatype.org/content/repositories/snapshots/).


# Usage

## Initialization

Prior to using Island Time, it may be initialized with custom providers for time zone rules or localized text. The platform default providers will be used for any that aren't specified explicitly. It's only necessary to initialize Island Time if you're using custom providers.

```kotlin
IslandTime.initialize {
    // Here, we're overriding all of the platform default providers with our own
    timeZoneRulesProvider = MyTimeZoneRulesProvider
    dateTimeTextProvider = MyDateTimeTextProvider
    timeZoneTextProvider = MyTimeZoneTextProvider
}
```

On Android, using the default provider will trigger an exception at runtime with any version below API 26 -- unless you're using Android Studio 4.0 or above with `coreLibraryDesugaringEnabled = true`. Until AS 4.0 and java.time desugaring becomes stable, it's recommended that you use the `AndroidThreeTenProvider` in `threetenabp-extensions` instead, which uses the Android JSR-310 backport under the hood to provide time zone data. Generally, initialization should be performed during `Application.onCreate()`.

```kotlin
// Note that it isn't necessary to call AndroidThreeTen.init() separately
IslandTime.initialize {
    timeZoneRulesProvider = AndroidThreeTenProvider(context)
}
```

For further information on the Android backport, see https://github.com/JakeWharton/ThreeTenABP.

## For Those Familiar with java.time

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

Unless you're doing calculations with particularly long durations at a high precision where overflow is a very real possibility, you might not need to use the `Duration` class at all. The ability to do this in an efficient manner is really enabled by Kotlin's inline classes.

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

Ultimately, support for format strings may be added in addition to the DSL-based approach, but there are readability and IDE discoverability advantages to the DSL.

## Examples

The following examples demonstrate how you might use Island Time to perform various tasks.

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

Periods are a date-based measure of time. Like java.time, Island Time trys to separate date-based and time-based measurements.

```kotlin
val period = periodOf(5.years, 13.months, 10.days)
val normalizedPeriod = period.normalized() // periodOf(6.years, 1.months, 10.days)
val modifiedPeriod = period - 1.years - 15.days // periodOf(5.years, 1.months, (-5).days)
val invertedPeriod = -period // periodOf((-5).years, (-13).months, (-10).days)
```

### Date Operators

```kotlin
val today = Date.now()
val tomorrow = today + 1.days
val nextWednesday = today.next(DayOfWeek.WEDNESDAY)
val lastSundayOrToday = today.previousOrSame(DayOfWeek.SUNDAY)
val startOfMonth = today.startOfMonth
val endOfMonth = today.endOfMonth

// Date range of the week assuming the ISO week definition (starts on Monday)
val isoWeekRange: DateRange = today.weekRange

// Saturday, Sunday, or Monday start according to the system settings
val defaultWeekRange: DateRange = today.weekRange(WeekSettings.systemDefault())
```

### Writing to ISO-8601 Representation

ISO-8601 is the international standard for dates and times. In Island Time, calling `toString()` on any date-time primitive will produce an ISO representation.

```kotlin
val isoTimestamp = Instant.now().toString() // 2019-10-28T08:34:03.389Z
val isoDuration = durationOf(5.hours + 4.seconds).toString() // PT5H4S
```

### Localized Text

```kotlin
// These examples assume a default locale of "en_US"
val nyZone = TimeZone("America/New_York")
val shortStandardName = nyZone.localizedName(TimeZoneTextStyle.SHORT_STANDARD) // EST
val genericName = nyZone.localizedName(TimeZoneTextStyle.GENERIC) // Eastern Time

val monthName = Month.JANUARY.displayName(TextStyle.LONG) // January
val dowName = DayOfWeek.FRIDAY.localizedText(TextStyle.SHORT) // Fri

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
    anyOf({ +'/' }, { +' ' })
    dayOfMonth(2)
    anyOf({ +'/' }, { +' ' })
    year(4)
}

val date = "10/01/2019".toDate(customParser)
```

### Date Ranges

Note that in Island Time, "ranges" are inclusive, implementing Kotlin's `ClosedRange`, while "intervals" are half-open with an exclusive end. When it comes to time, precision differences (ie. millisecond vs nanosecond) can make an inclusive end problematic, so while you can create an interval from a closed range, it'll be stored, read, and written with an exclusive end.

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

### Time Intervals

```kotlin
val now: Instant = Instant.now()
val then: Instant = now + 1.hours

// Step over instants in an interval in second increments
for (instant in now until then step 1.seconds) {
    // ...
}
```

### Open Ranges and Intervals

Island Time supports unbounded ranges and time intervals using the `MIN` and `MAX` values for the date-time primitive to indicate "far past" or "far future". In the ISO standard, this is referred to as an "open" interval, but that conflicts with the mathematical definition of open/closed (and Kotlin's `ClosedRange`), so we've opted not to use that terminology.

```kotlin
val instantInterval = "2008-09-01T04:00Z/..".toInstantInterval()

val isBounded = instantInterval.isBounded() // false
val hasUnboundedEnd = instantInterval.hasUnboundedEnd() // true
val duration = instantInterval.asDuration() // throws DateTimeException
```

### `at` Builders

Date-time primitives can be "built up" using the `at` infix function.

```kotlin
val clock = SystemClock()
val today = Date.now(clock)
val dateTime = today at Time.NOON
val zonedDateTime = dateTime at clock.zone()
val offsetDateTime = dateTime at UtcOffset.UTC

val anotherZonedDateTime = someInstant at TimeZone("America/New_York")
val yearMonth = Year(2019) at Month.NOVEMBER
```

### Daylight Savings Changes

`ZonedDateTime` is aware of time zone rules and handles daylight savings transitions as demonstrated below.

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

A set of extensions are available that allow you to convert to and from platform date-time types.

java.time / ThreeTenABP:
```kotlin
val javaLocalDate = Date(2019, OCTOBER, 24).toJavaLocalDate()
val javaDuration = 5.hours.toJavaDuration()
val islandDate = LocalDate.of(2019, OCTOBER, 24).toIslandDate()
```

iOS:
```kotlin
val nsDate = Instant.now().toNSDate()
val islandInstant = NSDate().toIslandInstant()
```

## Notes on kotlin.time

An [experimental time API](https://github.com/Kotlin/KEEP/issues/190) has recently been added to the Kotlin standard library. Unfortunately, its design does't mesh well with Island Time -- at least currently.

The Kotlin `Duration` class is based on a floating point number, which we steer clear of to avoid any accuracy issues that might come about during manipulation of floating point values and offer a fixed nanosecond precision across the entire supported time scale. We also opt to preserve unit granularity. For example, `1.seconds` translates to `IntSeconds` rather than `Duration`. This allows you to enforce a certain precision level when required.

At this time, Kotlin's `WallClock` isn't available and doesn't offer time zone support in any case, so we have our own `Clock` implementation as well. Island Time takes an extension-oriented approach to clocks though, so different implementations may be introduced in the future and support for any standard library clock will be added, if suitable.

# Feedback/Contributions

As mentioned earlier, the goal of this project is not just to port the java.time library over to Kotlin Multiplatform, but to take full advantage of Kotlin language features to create a date-time DSL that feels natural to users of the language and encourages best practices where possible. To that end, any and all feedback would be much appreciated in helping to iron out the API.

If you're interested in contributing or have ideas on areas that can be improved (there are definitely many right now), please feel free to initiate a dialog by opening design-related issues or submitting pull requests.
