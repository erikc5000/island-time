package io.islandtime.format

import io.islandtime.base.BooleanProperty
import io.islandtime.base.NumberProperty
import io.islandtime.base.TemporalProperty
import io.islandtime.base.getOrElse

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

fun <T> ConditionalFormatterBuilder<T>.onlyIfPresent(property: TemporalProperty<*>, builder: T.() -> Unit) {
    return onlyIf({ temporal.has(property) }, builder)
}

fun <T> ConditionalFormatterBuilder<T>.onlyIfAbsent(property: TemporalProperty<*>, builder: T.() -> Unit) {
    return onlyIf({ !temporal.has(property) }, builder)
}

fun <T> ConditionalFormatterBuilder<T>.onlyIfPresentAndNonZero(property: NumberProperty, builder: T.() -> Unit) {
    return onlyIf({ temporal.getOrElse(property) { 0L } != 0L }, builder)
}

fun <T> ConditionalFormatterBuilder<T>.onlyIfTrue(property: BooleanProperty, builder: T.() -> Unit) {
    return onlyIf({ temporal.get(property) }, builder)
}

fun <T> ConditionalFormatterBuilder<T>.onlyIfFalse(property: BooleanProperty, builder: T.() -> Unit) {
    return onlyIf({ !temporal.get(property) }, builder)
}
