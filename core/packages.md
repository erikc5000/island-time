# Module core

The core set of date-time classes, supporting the ISO calendar system.

# Package io.islandtime

Date-time primitives and core concepts, such as `Date`, `Time`, `Instant`, and `ZonedDateTime`.

# Package io.islandtime.base

**Experimental**: Framework-level interfaces, allowing aspects of date and time to be abstracted. This area is unstable and likely to see significant change.

# Package io.islandtime.calendar

Platform-independent calendar properties.

# Package io.islandtime.clock

The default clock implementation, providing access to the system clock at millisecond precision and a `FixedClock` for testing purposes.

# Package io.islandtime.darwin

Various extensions specifically for the Apple platform.

# Package io.islandtime.format

Classes involved in the formatting of dates and times.

# Package io.islandtime.jvm

Various extensions specifically for the JVM.

# Package io.islandtime.locale

Platform-independent locale.

# Package io.islandtime.measures

Classes related to the measurement of time, including `Duration`, `Period`, and more specific units, such as `IntHours` or `LongYears`.

# Package io.islandtime.operators

A set of convenience operators for various date-time primitives, enabling things like `date.next(TUESDAY)`, `date.startOfWeek`, or `dateTime.truncatedTo(HOURS)`.

# Package io.islandtime.parser

A set of predefined parsers that can be used to convert strings in various ISO formats into date-time primitives, along with a parsing engine that provides the ability to define custom parsers.

# Package io.islandtime.ranges

Date ranges, time intervals, and the ability to iterate over them and perform various operations.

# Package io.islandtime.zone

Time zone database support.