package io.islandtime.formatter.dsl

import io.islandtime.base.BooleanProperty
import io.islandtime.base.NumberProperty
import io.islandtime.base.TemporalProperty
import io.islandtime.base.getOrElse
import io.islandtime.format.dsl.IslandTimeFormatDsl
import io.islandtime.formatter.TemporalFormatter

@IslandTimeFormatDsl
interface ComposableFormatterBuilder {
    /**
     * Appends a formatter that has been defined outside of this builder.
     */
    fun use(formatter: TemporalFormatter)
}

@IslandTimeFormatDsl
interface ConditionalFormatterBuilder<T> {
    /**
     * Performs the formatting steps defined in [builder] only if [predicate] is satisfied.
     *
     * This can be used, for example, to check if a particular [TemporalProperty] is present on the object being
     * formatted.
     */
    fun onlyIf(predicate: TemporalFormatter.Context.() -> Boolean, builder: T.() -> Unit)
}

/**
 * Performs the formatting steps defined in [builder] only if the [property] is present.
 */
fun <T> ConditionalFormatterBuilder<T>.onlyIfPresent(property: TemporalProperty<*>, builder: T.() -> Unit) {
    return onlyIf({ temporal.has(property) }, builder)
}

/**
 * Performs the formatting steps defined in [builder] only if the [property] is absent.
 */
fun <T> ConditionalFormatterBuilder<T>.onlyIfAbsent(property: TemporalProperty<*>, builder: T.() -> Unit) {
    return onlyIf({ !temporal.has(property) }, builder)
}

/**
 * Performs the formatting steps defined in [builder] only if the [property] is present and not equal to zero.
 */
fun <T> ConditionalFormatterBuilder<T>.onlyIfPresentAndNonZero(property: NumberProperty, builder: T.() -> Unit) {
    return onlyIf({ temporal.getOrElse(property) { 0L } != 0L }, builder)
}

/**
 * Performs the formatting steps defined in [builder] only if the [property] is `true`.
 */
fun <T> ConditionalFormatterBuilder<T>.onlyIfTrue(property: BooleanProperty, builder: T.() -> Unit) {
    return onlyIf({ temporal.get(property) }, builder)
}

/**
 * Performs the formatting steps defined in [builder] only if the [property] is `false`.
 */
fun <T> ConditionalFormatterBuilder<T>.onlyIfFalse(property: BooleanProperty, builder: T.() -> Unit) {
    return onlyIf({ !temporal.get(property) }, builder)
}