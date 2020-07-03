# Parsing

## Predefined Parsers

Out of the box, Island Time can parse the most common ISO-8601 formats for dates, times, durations, and time intervals. The set of included parsers can be found in [`DateTimeParsers`](../api/core/io.islandtime.parser/-date-time-parsers/index.md).

The table below illustrates how the parsers for the various ISO formats are organized within `DateTimeParsers`, using the calendar date format as an example:

| Iso Format | Parser | Acceptable Input(s) |
| --- | --- | --- |
| Basic | `DateTimeParsers.Basic.CALENDAR_DATE` | `20200101` |
| Extended | `DateTimeParsers.Extended.CALENDAR_DATE` | `2020-01-01` |
| Any | `DateTimeParsers.CALENDAR_DATE` | `20200101` or `2020-01-01` |

The extended format is &mdash; by far &mdash; the most common. If you don't specify a parser explicitly when converting a string to an Island Time type, it will look for extended format only. Below are some examples:

```kotlin
// Parse an extended format date-time
val extendedDateTime = "2020-12-31T13:45".toDateTime()

// Parse a basic format date-time
val basicDateTime = "20201231T1345".toDateTime(DateTimeParsers.Basic.DATE_TIME)

// Parse an ordinal date (year and day of year)
val ordinalDate = "2020-365".toDate(DateTimeParsers.Extended.ORDINAL_DATE)
```

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