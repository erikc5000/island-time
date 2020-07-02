# Time Intervals

Kotlin offers first class support for ranges and Island Time takes full advantage of that, allowing you to model date ranges and time intervals in a way that feels natural.

## Nomenclature: "Ranges" vs. "Intervals"

In Island Time, "ranges" are inclusive, implementing Kotlin's `ClosedRange`, while "intervals" are half-open with an exclusive end. When representing time-based intervals, precision differences (ie. millisecond vs nanosecond) can make an inclusive end troublesome to work with, so while you can create an interval from a closed range, it'll be stored, read, and written with an exclusive end.

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

## Time-Based Intervals

Each of the [date-time classes](dates-and-times.md) has a corresponding interval class.

| Class | Example ISO Representation |
| --- | --- |
| `DateTimeInterval` | `2020-04-15T10:00/2020-04-15T13:00` |
| `InstantInterval` | `2020-04-15T10:00Z/2020-04-15T13:00Z` |
| `OffsetDateTimeInterval` | `2020-03-09T14:00-05:00/2020-03-10T17:00-04:00` |
| `ZonedDateTimeInterval` | `2020-04-15T10:00-04:00[America/New_York]/2020-04-15T21:30+01:00[Europe/London]` |

Inclusivity within a time interval is based on the instants defined by the start and end points (ie. timeline order, not natural order), which is in an important consideration for `OffsetDateTimeInterval` and `ZonedDateTimeInterval`, where there may be differing offsets.

### Iterating over time intervals

Only `InstantInterval` allows iteration, though the other types can be converted easily.

```kotlin
val now: ZonedDateTime = ZonedDateTime.now()
val zonedDateTimeInterval: ZonedDateTimeInterval = now until now + 1.weeks
val instantInterval: InstantInterval = zonedDateTimeInterval.asInstantInterval()
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

A range or interval may be unbounded on one or both ends -- or empty. The `MIN` and `MAX` sentinels can be used to indicate the "far past" or "far future".

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

!!! note ""Unbounded" vs. "Open""
    In ISO-8601, an "unbounded" interval is referred to as an "open" interval. This conflicts with the mathematical meaning of "open" though (ie. end points that are exclusive rather than inclusive), so we avoid using that terminology.

## ISO Representation

As with all of the other types in Island Time, calling `toString()` on an interval will return an ISO represention, which can be converted back to the appropriate interval type using methods like `String.toDateRange()`.

```kotlin
val firstDate = Date(2020, MARCH, 1)
val secondDate = Date(2020, MAY, 13)
val dateRange: DateRange = firstDate..secondDate

val isoDateRangeString = dateRange.toString()
// Output: 2020-03-01/2020-05-13

val readDateRange = isoDateRangeString.toDateRange()

val zone = TimeZone("America/New_York")
val zonedInterval: ZonedDateTimeInterval = dateRange.toZonedDateTimeIntervalAt(zone)
val isoZonedIntervalString = instantInterval.toString()
// Output: 2020-03-01T00:00-05:00[America/New_York]/2020-05-13T23:59:59.999999999-04:00[America/New_York]

val readZonedInterval = isoZonedIntervalString.toZonedDateTimeInterval()
```

By default, Island Time parses only ISO-8601 extended format, but [predefined parsers](../api/core/io.islandtime.parser/-date-time-parsers/index.md) are also available that can read the less common basic format -- or either format.