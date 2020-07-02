# Island Time

Island Time is a [Kotlin Multiplatform](https://kotlinlang.org/docs/reference/multiplatform.html) library for working
with dates and times. Heavily inspired by the java.time library, Island Time aims to provide a powerful API that works
across platforms while taking full advantage of the features offered by the Kotlin language.

## Features

- A full set of date-time primitives such as `Date`, `Time`, `DateTime`, `Instant`, and `ZonedDateTime`
- Time zone database support
- Date ranges and time intervals, integrating with Kotlin ranges and progressions
- Read and write strings in ISO formats
- DSL-based definition of custom parsers
- Access localized text for names of months, days of the week, time zones, etc.
- Convenience operators like `date.next(MONDAY)`, `dateTime.startOfWeek`, or `date.weekRange(WeekSettings.systemDefault())`
- Convert to and from platform-specific date-time types
- Works on JVM, Android, iOS, macOS, tvOS, and watchOS

## Current Limitations
- No custom format strings (must write platform-specific code to do this)
- Doesn't support all week-related fields or week-based dates
- Only supports the ISO calendar system