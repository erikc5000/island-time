package io.islandtime.parser

import io.islandtime.base.*

/**
 * The result of a parsing operation.
 */
class ParseResult(
    @PublishedApi
    internal val properties: MutableMap<TemporalProperty<*>, Any> = hashMapOf()
) : Temporal {
    fun isEmpty() = properties.isEmpty()
    fun isNotEmpty() = !isEmpty()
    val propertyCount: Int get() = properties.size

    inline fun <reified T> override(property: TemporalProperty<*>, value: T) {
        properties[property] = value as Any
    }

    inline operator fun <reified T> set(property: TemporalProperty<T>, value: T) {
        val previousValue = properties.put(property, value as Any)

        if (previousValue != null && previousValue != value) {
            throw TemporalParseException("Conflicting values were found for $property: '$previousValue' vs. '$value'")
        }
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

    override fun toString(): String {
        return "TemporalParseResult[$properties]"
    }

    internal fun deepCopy() = ParseResult(properties.toMutableMap())
}
