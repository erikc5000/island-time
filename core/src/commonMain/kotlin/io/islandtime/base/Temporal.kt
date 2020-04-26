package io.islandtime.base

import io.islandtime.DateTimeException

/**
 * A property of a date, time, or measurement of time.
 *
 * These are intended to be used primarily during parsing and formatting. Each date-time primitive is capable of
 * resolving or supplying the set of properties that are applicable to it.
 */
interface TemporalProperty<T>

/**
 * A property associated with a `Boolean` value.
 */
interface BooleanProperty : TemporalProperty<Boolean>

/**
 * A property associated with a `Long` value.
 */
interface NumberProperty : TemporalProperty<Long> {
    /**
     * The maximum range of possible values in the ISO calendar system.
     */
    val valueRange: LongRange get() = Long.MIN_VALUE..Long.MAX_VALUE
}

/**
 * A property associated with an object value.
 */
interface ObjectProperty<T> : TemporalProperty<T>

/**
 * A property associated with a `String` value.
 */
typealias StringProperty = ObjectProperty<String>

class TemporalPropertyException(
    message: String? = null,
    cause: Throwable? = null
) : DateTimeException(message, cause)

/**
 * A framework-level interface providing access to the properties of a date, time, or measurement of time.
 */
interface Temporal {
    /**
     * Check if [property] is available.
     */
    fun has(property: TemporalProperty<*>): Boolean = false

    /**
     * Get the value of [property].
     * @throws TemporalPropertyException if the property isn't available
     */
    fun get(property: BooleanProperty): Boolean {
        throwUnsupportedTemporalPropertyException(property)
    }

    /**
     * Get the value of [property].
     * @throws TemporalPropertyException if the property isn't available
     */
    fun get(property: NumberProperty): Long {
        throwUnsupportedTemporalPropertyException(property)
    }

    /**
     * Get the value of [property].
     * @throws TemporalPropertyException if the property isn't available
     */
    fun <T> get(property: ObjectProperty<T>): T {
        throwUnsupportedTemporalPropertyException(property)
    }
}

/**
 * Get the value of [property] or the result of [defaultValue] if it isn't available.
 */
inline fun Temporal.getOrElse(
    property: BooleanProperty,
    defaultValue: (property: BooleanProperty) -> Boolean
): Boolean {
    return if (has(property)) {
        get(property)
    } else {
        defaultValue(property)
    }
}

/**
 * Get the value of [property] or the result of [defaultValue] if it isn't available.
 */
inline fun Temporal.getOrElse(
    property: NumberProperty,
    defaultValue: (property: NumberProperty) -> Long
): Long {
    return if (has(property)) {
        get(property)
    } else {
        defaultValue(property)
    }
}

/**
 * Get the value of [property] or the result of [defaultValue] if it isn't available.
 */
inline fun <T> Temporal.getOrElse(
    property: ObjectProperty<T>,
    defaultValue: (property: ObjectProperty<T>) -> T
): T {
    return if (has(property)) {
        get(property)
    } else {
        defaultValue(property)
    }
}

/**
 * Get the value of [property] or `null`` if it isn't available.
 */
fun Temporal.getOrNull(property: BooleanProperty): Boolean? {
    return if (has(property)) get(property) else null
}

/**
 * Get the value of [property] or `null`` if it isn't available.
 */
fun Temporal.getOrNull(property: NumberProperty): Long? {
    return if (has(property)) get(property) else null
}

/**
 * Get the value of [property] or `null`` if it isn't available.
 */
fun <T> Temporal.getOrNull(property: ObjectProperty<T>): T? {
    return if (has(property)) get(property) else null
}

internal fun throwUnsupportedTemporalPropertyException(property: TemporalProperty<*>): Nothing {
    throw TemporalPropertyException("'$property' is not supported")
}