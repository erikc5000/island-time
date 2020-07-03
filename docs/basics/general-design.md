# General Design

Being heavily inspired by the java.time library, Island Time tends to follow many of the same design principles.

## Immutability

All of the date-time primitives are immutable and thread-safe. Operations that manipulate a date, time, duration, or interval will always return a new object.

## Precision

Island Time uses integer rather than floating-point values, offering a fixed nanosecond precision across the entire supported time scale. This avoids any surprises that might emerge from the use of floating-point arithmetic and the reduction in precision that occurs when representing larger durations.

## Overflow Handling

When working with dates and times, overflow is almost never a behavior that you want. See [Y2k](https://en.wikipedia.org/wiki/Year_2000_problem) or [Time formatting and storage bugs](https://en.wikipedia.org/wiki/Time_formatting_and_storage_bugs). Island Time uses checked arithmetic throughout to detect overflow and throw exceptions rather than failing silently.

## Type-Safety

In general, Island Time tries to prevent nonsensical operations at compile time rather than runtime. To that end, you'll find that there are a lot more classes than there are in a number of other date-time libraries.