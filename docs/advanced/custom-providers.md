# Custom Providers

By default, Island Time uses platform APIs to access time zone database information and localized text. Each platform and version of that platform exposes different information though, so there are compromises involved and Island Time may not always behave the way you'd like. Using custom providers, you can work around certain edge cases or make use of different data sources.

## Initialization

Prior to using Island Time, it may be initialized with custom providers for time zone rules or localized text. The platform default providers will be used for any that aren't specified explicitly. It's only necessary to initialize Island Time if you're using custom providers.

```kotlin
IslandTime.initialize {
    // Override all of the platform default providers with our own
    timeZoneRulesProvider = MyTimeZoneRulesProvider
    dateTimeTextProvider = MyDateTimeTextProvider
    timeZoneTextProvider = MyTimeZoneTextProvider
}
```

Island Time can only be initialized once. Subsequent attempts to initialize it will throw an exception. This is intended to alert you to potentially undesirable behavior as a result of late initialization or attempts to use different providers in different places. In a test environment though, this can sometimes be problematic, so you may explciitly restore Island Time to an unitialized state using the `reset()` function:

```kotlin
IslandTime.reset()
// It's now safe to initialize Island Time again
```
