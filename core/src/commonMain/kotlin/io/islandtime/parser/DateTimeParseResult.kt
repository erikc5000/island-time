package io.islandtime.parser

import io.islandtime.base.TemporalProperty

/**
 * The result of a parsing operation.
 */
inline class DateTimeParseResult(
    @PublishedApi
    internal val properties: MutableMap<TemporalProperty<*>, Any> = hashMapOf()
) {
    fun isEmpty() = properties.isEmpty()
    fun isNotEmpty() = !isEmpty()
    val size: Int get() = properties.size

    inline operator fun <reified T> set(property: TemporalProperty<T>, value: T) {
        properties[property] = value as Any
    }

    inline operator fun <reified T> get(property: TemporalProperty<T>): T? = properties[property] as T?

    internal fun deepCopy() = DateTimeParseResult(properties.toMutableMap())
}