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
 * A property associated with a `Long` value.
 */
interface NumberProperty : TemporalProperty<Long> {
    /**
     * The maximum range of possible values in the ISO calendar system.
     */
    val valueRange: LongRange get() = Long.MIN_VALUE..Long.MAX_VALUE
}

/**
 * A property associated with a `Boolean` value.
 */
interface BooleanProperty : TemporalProperty<Boolean>

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
    fun has(property: TemporalProperty<*>): Boolean = false

    fun <T> get(property: ObjectProperty<T>): T {
        throwUnsupportedTemporalPropertyException(property)
    }

    fun get(property: BooleanProperty): Boolean {
        throwUnsupportedTemporalPropertyException(property)
    }

    fun get(property: NumberProperty): Long {
        throwUnsupportedTemporalPropertyException(property)
    }
}

internal fun throwUnsupportedTemporalPropertyException(property: TemporalProperty<*>): Nothing {
    throw TemporalPropertyException("'$property' is not supported")
}