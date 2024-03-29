# Dates and Times

Island Time has a wide array of different date-time classes, each tailored to its own set of use cases. These classes model the date and timekeeping system defined in [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601), the international standard for the exchange of dates and times. The ISO standard applies the present-day Gregorian calendar _proleptically_, which is to say, even to the time period before it was [adopted](https://en.wikipedia.org/wiki/Adoption_of_the_Gregorian_calendar).

## Date Representations

| Class | Precision | Example ISO Representation |
| --- | --- | --- |
| [`Date`](../api/core/core/io.islandtime/-date/index.md) | day | `2020-02-15` |
| [`YearMonth`](../api/core/core/io.islandtime/-year-month/index.md) | month | `2020-02` |
| [`Year`](../api/core/core/io.islandtime/-year/index.md) | year | `2020` |

The [`Date`](../api/core/core/io.islandtime/-date/index.md) class represents a date in an ambiguous region. It could be in New York City, it could be in Tokyo. The instants in time that define the start and end of a `Date` can only be determined in the context of a particular time zone &mdash; hence the _ambiguous_ part.

```kotlin
// Get the current date in the local time zone of the system
val today = Date.now()

// Create a date from components
val leapDay = Date(2020, Month.February, 29)

// Parse a date from ISO string representation
val cincoDeMayo = "2020-05-05".toDate()
```

It's also possible to represent a date with reduced precision. For example, a [`YearMonth`](../api/core/core/io.islandtime/-year-month/index.md) could be used to represent a credit card expiration date containing just a year and month.

```kotlin
// Create a year-month from a year and month
val expiration = YearMonth(year, month)

// A year-month can also be built using the "at" operator
val yearMonth: YearMonth = Year(2020) at Month.AUGUST
```

## Time of Day

The [`Time`](../api/core/core/io.islandtime/-time/index.md) class can be used to represent a time of the day in an ambiguous region. Unlike `Date`, there are no classes with reduced precision &mdash; a `Time` is always precise to the nanosecond.

```kotlin
// Get the current time in the local time zone of the system
val currentTime = Time.now()

// Create a time from components
val time = Time(13, 59, 59, 999_999_999)

// Destructure a time back into components
val (hour, minute, second, nanosecond) = time
```

## Combined Date and Time of Day

A [`DateTime`](../api/core/core/io.islandtime/-date-time/index.md) combines a [`Date`](../api/core/core/io.islandtime/-date/index.md) and [`Time`](../api/core/core/io.islandtime/-time/index.md), allowing you to represent both in a single data structure, still in an ambiguous region.

```kotlin
// Create a date-time from individual date and time components
val dateTime = DateTime(2020, Month.JANUARY, 21, 1, 0)

// Destructure a date-time
val (date, time) = dateTime

val date = Date(2019, Month.MARCH, 15)

// The "at" operator can also be used to create a date-time
val anotherDateTime: DateTime = date at Time.NOON

// Or you could get the date-time at midnight from a date
val startOfDay: DateTime = date.startOfDay
```

There's no guarantee that a [`DateTime`](../api/core/core/io.islandtime/-date-time/index.md) will exist exactly once in a given time zone. Due to daylight savings time transitions, it may exist twice or it may not exist at all. We'll get into this more shortly, but it's important to keep in mind that working with and manipulating a [`DateTime`](../api/core/core/io.islandtime/-date-time/index.md) directly can lead to subtle bugs.

## Instants in Time

So far, the classes we've looked at model dates and times in an ambiguous region, but often we want to unambiguously capture an instant in time. There are three different classes in Island Time that can do this, each serving a different purpose.

| Class | Description |
| --- | --- |
| [`Instant`](../api/core/core/io.islandtime/-instant/index.md) | A timestamp |
| [`ZonedDateTime`](../api/core/core/io.islandtime/-zoned-date-time/index.md) | A date and time of day in a particular time zone |
| [`OffsetDateTime`](../api/core/core/io.islandtime/-offset-date-time/index.md) | A date and time of day with fixed UTC offset |

An [`Instant`](../api/core/core/io.islandtime/-instant/index.md) is simply a number of seconds and nanoseconds that have elapsed since the [Unix epoch](https://en.wikipedia.org/wiki/Unix_time) (`1970-01-01T00:00Z`), ignoring leap seconds. There's no concept of "date" without conversion to one of the other types. Practically speaking, this is the class you should use when you don't care about the local time and just want a [UTC](https://en.wikipedia.org/wiki/Coordinated_Universal_Time) timestamp.

```kotlin
data class DogDto(
    val name: String,
    val breed: String,
    // Capture the current system time
    val creationTime: Instant = Instant.now()
)
```

To capture an instant along with the local time, you have two options &mdash; [`OffsetDateTime`](../api/core/core/io.islandtime/-offset-date-time/index.md) and [`ZonedDateTime`](../api/core/core/io.islandtime/-zoned-date-time/index.md). Both store a `DateTime` along with a `UtcOffset`, however, `ZonedDateTime` is also aware of time zone rules, which is an important distinction.

### `TimeZone` vs. `UtcOffset`

In Island Time, a [`UtcOffset`](../api/core/core/io.islandtime/-utc-offset/index.md) is just a number of seconds that a local time must be adjusted forward or backward by to be equivalent to UTC. A [`TimeZone`](../api/core/core/io.islandtime/-time-zone/index.md) defines the rules used to determine the UTC offset. Time zones fall into two categories &mdash; region-based (`TimeZone.Region`) and fixed offset (`TimeZone.FixedOffset`).

Region-based zones have identifiers, such as "America/New_York" or "Europe/London", that correspond to entries in the [IANA Time Zone Database](https://www.iana.org/time-zones).

Fixed offset zones have a fixed UTC offset. While region-based zones are generally preferrable, a suitable one may not exist in all situations.

### `ZonedDateTime` vs. `OffsetDateTime`

While most platforms nowadays draw their understanding of time zones from the [IANA Time Zone Database](https://www.iana.org/time-zones), time zones and their rules change all the time and different systems might have different versions of the database or only a subset of it available. This makes persistance and serialization of `ZonedDateTime` troublesome since there's the possibility that when the stored data gets read later, the zone can't be found or its rules have changed, thus altering the local date and time.

Using `OffsetDateTime` guarantees that you'll never get an exception due to an unavailable time zone and that the value you save will be the value that's read later, making it well-suited for this particular use case. More often than not, you should use `ZonedDateTime` since it will handle daylight savings transitions correctly when doing any sort of calendar math, but you may want to consider converting to an `OffsetDateTime` when you persist or serialize your data.

```kotlin
val date = Date(2020, Month.MARCH, 8)
val time = Time(2, 30)
val zone = TimeZone("America/New_York")

// 2:00 on March 8 marks the beginning of daylight savings time on the east
// coast of the United States, so 2:30 doesn't exist. The time is automatically
// adjusted by an hour to 3:30.
val zonedDateTime = date at time at zone
println(zonedDateTime)
// Output: 2020-03-08T03:30-04:00 [America/New_York]

// If we subtract an hour, the offset will revert to that of standard time
println(zonedDateTime - 1.hours)
// Output: 2020-03-08T01:30-05:00 [America/New_York]

// It's easy to convert a ZonedDateTime to an OffsetDateTime
println(zonedDateTime.toOffsetDateTime())
// Output: 2020-03-08T03:30-04:00

// It's also possible to change the time zone such that it uses a fixed offset
// instead of "America/New_York", making it functionally equivalent to an
// OffsetDateTime.
println(zonedDateTime.withFixedOffsetZone())
// Output: 2020-03-08T03:30-04:00

// Or change the zone while preserving the captured instant
println(zonedDateTime.adjustedTo(TimeZone("America/Los_Angeles")))
// Output: 2020-03-07T23:30-08:00
```

## Patterns, Properties, and Operators

Throughout Island Time's date-time primitives, you'll find a set of patterns that remain (relatively) constant, as well as a number of properties and operators that simplify common tasks.

### `at`

The `at` infix function can be used to build up date-time primitives from "smaller" pieces. For example, we can create a `DateTime` by combining a `Date` and a `Time`.

```kotlin
val dateTime = Date.now() at Time.NOON
```

We can then turn that into a `ZonedDateTime` by combining it with a `TimeZone`.

```kotlin
val zonedDateTime = dateTime at TimeZone.systemDefault()
```

### `copy()`

Similar to Kotlin's data classes, each date-time primitive has a `copy()` method available, making it easy to create a copy while changing any number of properties.

```kotlin
val dateTime = DateTime.now().copy(dayOfMonth = 15)
val dateTimeAtMidnight = dateTime.copy(time = Time.MIDNIGHT)
```

### Addition and subtraction

A [duration](durations.md) of time can be added or subtracted from a date-time primitive. Which units are supported will vary depending on whether the primitive is date-based, time-based, or both.

```kotlin
val tomorrow = Date.now() + 1.days
val yesterday = Date.now() - 1.days

val tenSecondsLater = Instant.now() + 10.seconds
```

When working with `ZonedDateTime`, adding a day-based period of time may cross a daylight savings time transition, in which case, adding `1.days` may not be the same as adding `24.hours`.

### Start and end of time periods

Relative to any date-based primitive, it's possible to get the start or end of a given period, be it the year, month, week, or day.

```kotlin
val date = Date.now()
val startOfYear = date.startOfYear
val endOfYear = date.endOfYear
val startOfMonth = date.startOfMonth
val startOfDay: DateTime = date.startOfDay
```

When it comes to weeks, we need to consider which day represents the start of the week. According to the ISO standard, that's Monday. However, depending on the locale, that may be on Sunday or Saturday instead. [`WeekSettings`](../api/core/core/io.islandtime.calendar/-week-settings/index.md) and the platform `Locale` type can be used to provide control over this.

```kotlin
// Start of ISO week (Monday start)
val isoStart = Date.now().startOfWeek

// Start of week using Sunday as start
val sundayStart = Date.now().startOfWeek(WeekSettings.SUNDAY_START)

// Respect the user's system settings (usually, most appropriate)
val systemStart = Date.now().startOfWeek(WeekSettings.systemDefault())

// Use the default associated with a particular locale
val localeStart = Date.now().startOfWeek(explicitLocale)
```

You can also get the week period as a [range or interval](intervals.md).

```kotlin
// Get the date range of the current week
val rangeOfWeek: DateRange = Date.now().week(WeekSettings.systemDefault())
```

### Previous or next day of week

To get, say, the previous Tuesday from a particular date-time, you can do something like this:

```kotlin
val now = ZonedDateTime.now()

// Get the Tuesday before "now" at the same time of day
val nowOnTuesday = now.previous(TUESDAY)

// Get the Tuesday before "now" or "now" if it already falls on a Tuesday
val nowOnTuesdayOrSame = now.previousOrSame(TUESDAY)
```

Similarly, you can use `next()` or `nextOrSame()` to get the next day of the week relative to the current date.

```kotlin
val today = Date.now()
val nextWednesday = today.next(WEDNESDAY)
val nextWednesdayOrToday = today.nextOrSame(WEDNESDAY)
```

### Rounding

A time or date-time can be rounded up, down, or half-up to the precision of a particular unit.

```kotlin
val dateTime = DateTime.now()
// Output: 2020-06-30T06:32:14.168

// Round half-up to the nearest minute
val roundedToMinute = dateTime.roundedTo(MINUTES)
// Output: 2020-06-30T06:32

// Round up to the nearest hour
val roundedUpToHour = dateTime.roundedUpTo(HOURS)
// Output: 2020-06-30T07:00

// Round down to the nearest hour (alternatively, you can use roundedDownTo())
val roundedDownToHour = dateTime.truncatedTo(HOURS)
// Output: 2020-06-30T06:00
```

You can also round to the nearest 15 minutes &mdash; or whatever increment you'd like.

```kotlin
// Round half-up to the nearest 15 minutes
val roundedToNearest15Mins = dateTime.roundedToNearest(15.minutes)
// Output: 2020-06-30T06:30

// Round down to the nearest 100 milliseconds
val roundedDownTo100Millis = dateTime.roundedDownToNearest(100.milliseconds)
// Output: 2020-06-30T06:32:14.1
```

### Week numbers

You can obtain the week number and year as defined in the [ISO week date system](https://en.wikipedia.org/wiki/ISO_week_date) like this:

```kotlin
val date = Date.now()

// Get the week-based year, which may differ from the regular year
val isoWeekYear: Int = date.weekBasedYear

// Get the week of the week-based year
val isoWeekNumber: Int = date.weekOfWeekBasedYear

// Or convert the date to a full ISO week date representation in a single step
date.toWeekDate { year: Int, week: Int, day: Int ->
    // ...
}
```

Different week definitions can be used by specifying the [`WeekSettings`](../api/core/core/io.islandtime.calendar/-week-settings/index.md) explicitly.

```kotlin
val usaWeekNumber: Int = date.weekOfWeekBasedYear(WeekSettings.SUNDAY_START)
```

!!! info "`weekOfYear` vs. `weekOfWeekBasedYear`"
    The week number associated with a particular date could fall in the prior or subsequent year, depending on how the week is defined. `weekOfYear` will return the week number relative to the date's regular `year` &mdash; `0` if it falls in the prior year, for example. On the other hand, `weekOfWeekBasedYear` will adjust the number to the `weekBasedYear`.

## ISO Representation

Any date-time primitive can be converted to an appropriate ISO string format by simply calling `toString()`. To convert a string into a date-time primitive, use the appropriate conversion function, such as `String.toDate()` or `String.toInstant()`.

```kotlin
val date = Date.now()
val isoString: String = date.toString
val dateFromString: Date = isoString.toDate()
```

By default, Island Time reads and writes using ISO-8601 extended format, which is most common. [Predefined parsers](parsing.md#predefined-parsers) are also available that can handle other formats.
