# Formatting

Currently, Island Time lacks the ability to do localized and custom formatting of dates and times in common code. This is in the works and should be available pretty soon. Right now though, it is still possible to access localized text in common code and platform-specific APIs can be used to handle formatting.

## Accessing Localized Text

You can obtain the localized name of a month, day of the week, or time zone in common code like so:

```kotlin
// Get the system default locale. We'll assume this is "en_US".
val locale = defaultLocale()

val shortMonth = FEBRUARY.localizedName(TextStyle.SHORT_STANDALONE, locale)
// Output: "Feb"

val fullDayOfWeek = TUESDAY.localizedName(TextStyle.FULL_STANDALONE, locale)
// Output: "Tuesday"

val tz = TimeZone("America/New_York")
val tzName = tz.displayName(TimeZoneTextStyle.DAYLIGHT, locale)
// Output: "Eastern Daylight Time"
```

In general, you'll find a `localizedName()` method that returns `null` if text is unavailable for the provided style and locale. And then a `displayName()` method that will instead return a default value if localized text is unavailable, such as the month or day of week number.

## `Locale`

Island Time's `Locale` is simply a `typealias` for `java.util.Locale` or `NSLocale`. The `defaultLocale()` method allows you to access the system default locale in common code. If you want to use a specific locale, you should create it in platform-specific code and inject it into your common code.

## Using Platform APIs

While you can't share all of your formatting-related code when using platform APIs, there are reasons why you may not necessarily want to do that anyway.

- Even though Island Time only supports the ISO calendar system, using platform APIs, you can still output to the user's preferred calendar

- You can better guarantee that formatting will be consistent with the user's expectations for the platform

- You can take advantage of localization features that may not be available or implemented consistently on all platforms

### Java/Android

```kotlin
val zone = TimeZone("America/New_York")
val instant = Instant.UNIX_EPOCH
val islandZonedDateTime = instant at zone

// Create a java.time DateTimeFormatter to do the formatting
val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL)

println(islandZonedDateTime.toJavaZonedDateTime().format(formatter))
// Output: "Wednesday, December 31, 1969 at 7:00:00 PM Eastern Standard Time"
```

### Apple

```kotlin
val zone = TimeZone("America/New_York")
val instant = Instant.UNIX_EPOCH

// Convert to an NSDate
val nsDate = instant.toNSDate()

// Create an NSDateFormatter to do the formatting
val formatter = NSDateFormatter().apply {
   dateStyle = NSDateFormatterFullStyle
   timeStyle = NSDateFormatterFullStyle
   timeZone = zone.toNSTimeZone()
}

println(formatter.stringFromDate(nsDate))
// Output: "Wednesday, December 31, 1969 at 7:00:00 PM Eastern Standard Time"
```