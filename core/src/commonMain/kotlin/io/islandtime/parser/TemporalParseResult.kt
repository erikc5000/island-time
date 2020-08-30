package io.islandtime.parser

import io.islandtime.base.*

/**
 * The result of a parsing operation.
 */
inline class TemporalParseResult(
    @PublishedApi
    internal val properties: MutableMap<TemporalProperty<*>, Any> = hashMapOf()
) : Temporal {
    fun isEmpty() = properties.isEmpty()
    fun isNotEmpty() = !isEmpty()
    val size: Int get() = properties.size

    inline operator fun <reified T> set(property: TemporalProperty<T>, value: T) {
        properties[property] = value as Any
    }

    inline operator fun <reified T> get(property: TemporalProperty<T>): T? = properties[property] as T?

    override fun has(property: TemporalProperty<*>): Boolean {
        return properties.containsKey(property) || super.has(property)
    }

    override fun get(property: BooleanProperty): Boolean {
        return properties[property]?.let { it as Boolean } ?: super.get(property)
    }

    override fun get(property: NumberProperty): Long {
        return properties[property]?.let { it as Long } ?: super.get(property)
    }

    override fun <T> get(property: ObjectProperty<T>): T {
        @Suppress("UNCHECKED_CAST")
        return properties[property]?.let { it as T } ?: super.get(property)
    }

    fun resolve(property: NumberProperty): Long {
        return get(property).also { properties[property] = it }
    }

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
