# Parsing

## Predefined Parsers

Out of the box, Island Time can parse the most common ISO-8601 formats for dates, times, durations, and time intervals. The set of included parsers can be found in [`DateTimeParsers`](../api/core/io.islandtime.parser/-date-time-parsers/index.md).

## Custom Parsers

In an ideal world, non-ISO formats wouldn’t exist, but sometimes they do and you need to parse them. To support that, you can define custom parsers using a DSL.

```kotlin
// Define a custom parser
val customParser = dateTimeParser {
    monthNumber()
    anyOf({ +'/' }, { +'-' })
    dayOfMonth()
    optional {
        anyOf({ +'/' }, { +'-' })
        year()
    }
}

// Parse a date using it
try {
    val date = "3/17/2020".toDate(customParser)
} catch (e: DateTimeException) {
    // ...
}
```

When dealing with ranges and intervals, you'll need to define a "grouped" parser, which can handle multiple results.

```kotlin
val customGroupedParser = groupedDateTimeParser {
    group {
        childParser(customParser)
    }
    +"--"
    group {
        childParser(customParser)
    }
}

val dateRange = "3/17/2020--4/5/2020".toDateRange(customGroupedParser)
```