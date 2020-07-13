[![Build Status](https://github.com/erikc5000/island-time/workflows/Publish/badge.svg)](https://github.com/erikc5000/island-time/actions?query=workflow%3APublish) [![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.islandtime/core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.islandtime/core)

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

See the [project website](https://islandtime.io) for more information along with the API reference docs.

## Feedback/Contributions

The goal of this project is not just to port the java.time library over to Kotlin Multiplatform, but to take full advantage of Kotlin language features to create a date-time DSL that feels natural to users of the language and encourages best practices where possible. To that end, any and all feedback would be much appreciated in helping to iron out the API.

If you're interested in contributing or have ideas on areas that can be improved (there are definitely many right now), please feel free to initiate a dialog by opening design-related issues or submitting pull requests.
