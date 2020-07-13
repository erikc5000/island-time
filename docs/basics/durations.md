# Durations

In Island Time, durations are fully type-safe. The use of durations in terms of a single unit is encouraged where possible. Date-based periods in terms of multiple units and long time-based durations are also available.

## Single Unit Durations

Island Time provides [inline classes](https://kotlinlang.org/docs/reference/inline-classes.html) representing each individual duration unit, backed by either a `Long` or `Int` &mdash; such as `IntYears`, `IntHours`, or `LongNanoseconds`. This allows the precision of each quantity to be maintained, avoids ambiguitity regarding the meaning of a day (ie. conceptual vs. 24 hours), and is quite efficient as well. When adding or subtracting quantities in mixed units, precision is increased automatically as needed. For example:

```kotlin
// The minimum necessary unit granularity is preserved when
// combining different units
val totalSeconds: IntSeconds = 5.hours + 30.minutes + 1.seconds

// Math with Int values on sub-second units forces a lengthing
// to Long due to overflow potential
val nanoseconds: LongNanoseconds = 5.seconds + 1.nanoseconds
```

A quantity in one unit can be broken down into parts in terms of "bigger" units using the `toComponents()` method.

```kotlin
61.minutes.toComponents { hours: IntHours, minutes: IntMinutes ->
   println(hours) // PT1H
   println(minutes) // PT1M
}
```

Or converted to another unit.

```kotlin
val hours: IntHours = 60.minutes.inHours
```

You can also get the duration between two date-times in terms of any given unit, using functions like `hoursBetween()` or `daysBetween()`.

```kotlin
val hours = hoursBetween(firstDateTime, secondDateTime)
val absHours = hours.absoluteValue // hours may be negative
```

## `Duration`

The [`Duration`](../api/core/io.islandtime.measures/-duration/index.md) class can be used to represent time-based durations that are potentially very large at nanosecond precision. In most cases, it's probably unnecessary and single unit durations will be perfectly satisfactory, but you can be assured that overflow won't happen when expressing any duration that fits within the supported time scale.

A single unit duration can be converted to a `Duration` like so:

```kotlin
val duration: Duration = 5.seconds.asDuration()
```

To construct a `Duration` from seconds and nanoseconds in a single step, you can use `durationOf()`.

```kotlin
val duration: Duration = durationOf(5.seconds, 100.nanoseconds)
```

The methods and operators available to `Duration` are mostly the same as those on the single unit durations.

```kotlin
val duration: Duration = durationBetween(firstDateTime, secondDateTime)
val minutes: LongMinutes = duration.inMinutes
```

## `Period`

A [`Period`](../api/core/io.islandtime.measures/-period/index.md) is a date-based measurement of time consisting of a number of years, months, and days. The code below shows some of the things that you can do with it.

```kotlin
val period: Period = periodOf(5.years, 13.months, 10.days)

// Convert months to years where appropriate
val normalizedPeriod = period.normalized() // 6.years, 1.months, 10.days

// Units can be added or subtracted
val modifiedPeriod = period - 1.years - 15.days // 5.years, 1.months, (-5).days

// Or inverted
val invertedPeriod = -period // (-5).years, (-13).months, (-10).days

// The period can also be destructured
val (years, months, days) = period

// We can also get the period between two dates
val periodBetweenDates = periodBetween(date1, date2)
```

## ISO Representation

Similar to date-times, calling `toString()` on any duration will return an ISO representation, such as `PT23H12M` or `P0D`. `String.toDuration()` or `String.toPeriod()` can be used to convert an ISO duration string into an object of the corresponding type.

```kotlin
val period = periodOf(5.years, 13.months, (-10).days)
val isoString = period.toString() // P5Y13M-10D
val readPeriod = isoString.toPeriod()
```

Currently, there's no way to represent a full ISO duration consisting of both date and time components and it's not possible to parse a string directly to a single duration unit. Some changes in this area are planned for the future.