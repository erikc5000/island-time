package io.islandtime.parser

import io.islandtime.base.TemporalProperty

/**
 * The result of a parsing operation.
 */
inline class TemporalParseResult(
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

//    inline fun <reified T, R> replace(
//        existingProperty: TemporalProperty<T>,
//        newProperty: TemporalProperty<R>,
//        valueTransform: (T) -> R
//    ): Boolean {
//        return if (existingProperty in properties) {
//            val newValue = valueTransform(properties[existingProperty])
//
//        } else {
//            false
//        }
//    }

    internal fun deepCopy() = TemporalParseResult(properties.toMutableMap())
}