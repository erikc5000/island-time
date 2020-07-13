# Ranges and Intervals

Kotlin offers first class support for ranges and Island Time takes full advantage of that, allowing you to model date ranges and time intervals in a way that feels natural.

## Terminology: "Ranges" vs. "Intervals"

In Island Time, "ranges" are inclusive, implementing Kotlin's [`ClosedRange`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.ranges/-closed-range/) interface, while "intervals" are half-open with an exclusive end. When representing time-based intervals, precision differences (ie. millisecond vs. nanosecond) can make an inclusive end troublesome to work with, so while you can create an interval from a closed range, it'll be stored, read, and written with an exclusive end.

## `DateRange`

A range of dates can be represented by a `DateRange`, which is also a [progression](https://kotlinlang.org/docs/reference/ranges.html#progression), allowing you to iterate over each day in the range. Using a custom `step`, you can iterate with an increment of any number of days, weeks, months, or years. You can also use `until` and `downTo` just like you can with Kotlin's built-in types.

```kotlin
val clock: Clock = SystemClock()
val today: Date = Date.now(clock)

// Create a date range
val dateRange: DateRange = today - 1.months..today

// Iterate over each day in the range
for (date in dateRange) {
    val startOfDay: ZonedDateTime = date.startOfDayAt(clock.zone)
    val endOfDay: ZonedDateTime = date.endOfDayAt(clock.zone)
    // ...
}

// Step by months instead of days
for (date in today until today + 1.years step 1.months) {
   // ...
}
```

Additional operations are also supported, such as `random()` and the ability to get the length of the range in terms of whatever unit you'd like.

```kotlin
// Pick a random date
val randomDate = (today..today + 1.months).random()

// Get the total number of days
val totalDays: LongDays = (today until today + 6.months).lengthInDays

// Get the Period represented by the range
val period: Period = (today..today + 1.months).asPeriod()
```

## Time Intervals

Each of Island Time's [date-time](dates-and-times.md) classes has a corresponding interval class.

| Class | Example ISO Representation |
| --- | --- |
| `DateTimeInterval` | `2020-04-15T10:00/2020-04-15T13:00` |
| `InstantInterval` | `2020-04-15T10:00Z/2020-04-15T13:00Z` |
| `OffsetDateTimeInterval` | `2020-03-09T14:00-05:00/2020-03-10T17:00-04:00` |
| `ZonedDateTimeInterval` | `2020-04-15T10:00-04:00[America/New_York]/2020-04-15T21:30+01:00[Europe/London]` |

For `OffsetDateTimeInterval` and `ZonedDateTimeInterval`, inclusivity within a time interval is based on the instants defined by the start and end points, ignoring any local time differences (ie. timeline order, not natural order).

### Converting a `DateRange` to an interval

A `DateRange` can be converted directly to an interval representing the period from the start of the first day to the end of the last day.

```kotlin
val today: Date = Date.now()
val dateRange: DateRange = today - 1.weeks until today
val zone: TimeZone = TimeZone.systemDefault()

// Convert to a ZonedDateTimeInterval
val zonedDateTimeInterval: ZonedDateTimeInterval = dateRange at zone

// Convert to an InstantInterval
val instantInterval: InstantInterval = dateRange.toInstantIntervalAt(zone)
```

### Iterating over intervals

Only `InstantInterval` allows iteration, though the other interval types can be converted easily enough.

```kotlin
val now: ZonedDateTime = ZonedDateTime.now()
val zonedDateTimeInterval: ZonedDateTimeInterval = now until now + 1.weeks
val instantInterval: InstantInterval = zonedDateTimeInterval.toInstantInterval()
```

Unlike with date ranges, the `step` is necessary to create a progression.

```kotlin
val now = Instant.now()
val then = now + 1.hours

for (instant in now until then step 1.seconds) {
   // ...
}
```

## Unbounded and Empty Intervals

A range or interval may be unbounded on one or both ends &mdash; or empty. The `MIN` and `MAX` sentinels can be used to indicate the "far past" or "far future".

```kotlin
val partiallyBoundedDateRange = "2020-04-12/..".toDateRange()
assertFalse { partiallyBoundedDateRange.isBounded() }

// Most operations are not valid on unbounded ranges
assertFailsWith<UnsupportedOperationException> {
   partiallyBoundedDateRange.lengthInYears
}

// Range containing every representable date
val completelyUnbounded = DateRange.UNBOUNDED

// Range containing no dates
val emptyDateRange = DateRange.EMPTY
```

!!! info ""Unbounded" vs. "Open""
    In ISO-8601, an "unbounded" interval is referred to as an "open" interval. However, this conflicts with the mathematical meaning of "open" (ie. end points that are exclusive rather than inclusive), so we try to avoid using that terminology.

## ISO Representation

As with all of the other types in Island Time, calling `toString()` on an interval will return an ISO represention, which can be converted back to the appropriate interval type using methods like `String.toDateRange()` or `String.toZonedDateTimeInterval()`.

```kotlin
val firstDate = Date(2020, MARCH, 1)
val secondDate = Date(2020, MAY, 13)
val dateRange: DateRange = firstDate..secondDate

val isoDateRangeString = dateRange.toString()
// Output: 2020-03-01/2020-05-13

val readDateRange = isoDateRangeString.toDateRange()

val zone = TimeZone("America/New_York")
val zonedInterval: ZonedDateTimeInterval = dateRange at zone
val isoZonedIntervalString = zonedInterval.toString()
// Output: 2020-03-01T00:00-05:00[America/New_York]/2020-05-13T23:59:59.999999999-04:00[America/New_York]

val readZonedInterval = isoZonedIntervalString.toZonedDateTimeInterval()
```

By default, Island Time parses only ISO-8601 extended format, but [predefined parsers](parsing.md#predefined-parsers) are also available that can read the less common basic format &mdash; or either format.