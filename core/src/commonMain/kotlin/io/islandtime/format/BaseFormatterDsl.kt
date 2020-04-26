package io.islandtime.format

import io.islandtime.base.Temporal
import io.islandtime.base.TemporalProperty

@IslandTimeFormatDsl
interface ComposableFormatterBuilder {
    /**
     * Append a formatter that has been defined outside of this builder.
     */
    fun use(formatter: TemporalFormatter)
}

@IslandTimeFormatDsl
interface ConditionalFormatterBuilder<T> {
    /**
     * Perform the formatting steps defined in [builder] only if [predicate] is satisfied.
     *
     * This can be used, for example, to check if a particular [TemporalProperty] is present on the
     * object being formatted.
     */
    fun onlyIf(predicate: (temporal: Temporal) -> Boolean, builder: T.() -> Unit)
}