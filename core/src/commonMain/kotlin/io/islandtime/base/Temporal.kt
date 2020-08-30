package io.islandtime.base

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

/**
 * A number property whose value can be derived from other properties when not directly available.
 */
interface DerivableNumberProperty : NumberProperty {
    /**
     * Checks if this property cam be derived from other properties of [temporal].
     */
    fun isDerivableFrom(temporal: Temporal): Boolean

    /**
     * Derives the value of this property from other properties of [temporal].
     */
    fun deriveValueFrom(temporal: Temporal): Long
}

/**
 * A number property whose value can be derived within [Context] when not directly available.
 */
interface ContextualNumberProperty<Context> : NumberProperty {
    /**
     * Checks if this property cam be derived from other properties of [temporal].
     */
    fun isDerivableFrom(temporal: Temporal): Boolean

    /**
     * Derives the value of this property within a given [context] based on other properties of [temporal].
     */
    fun deriveValueFrom(temporal: Temporal, context: Context): Long
}

class TemporalPropertyException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * An object that provides access to properties of date, time, or measurement of time.
 */
interface Temporal {
    /**
     * Checks if [property] is available directly.
     */
    fun has(property: TemporalProperty<*>): Boolean {
        return false //return property is DerivableNumberProperty && property.isDerivableFrom(this)
    }

    /**
     * Gets the value of [property].
     * @throws TemporalPropertyException if the property isn't available
     */
    fun get(property: BooleanProperty): Boolean {
        throwUnsupportedTemporalPropertyException(property)
    }

    /**
     * Gets the value of [property].
     * @throws TemporalPropertyException if the property isn't available
     */
    fun get(property: NumberProperty): Long {
        return if (property is DerivableNumberProperty) {
            property.deriveValueFrom(this)
        } else {
            throwUnsupportedTemporalPropertyException(property)
        }
    }

    /**
     * Gets the value of [property].
     * @throws TemporalPropertyException if the property isn't available
     */
    fun <T> get(property: ObjectProperty<T>): T {
        throwUnsupportedTemporalPropertyException(property)
    }
}

fun Temporal.hasAll(vararg properties: TemporalProperty<*>): Boolean = properties.all { has(it) }

/**
 * Checks if [property] is either available directly or can be derived without any additional context.
 */
//fun Temporal.hasOrCanDerive(property: TemporalProperty<*>): Boolean {
//    return has(property) || (property is DerivableNumberProperty && property.isDerivableFrom(this))
//}

/**
 * Gets the value of [property] or the result of [defaultValue] if it isn't available.
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
 * Gets the value of [property] or the result of [defaultValue] if it isn't available.
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
 * Gets the value of [property] or the result of [defaultValue] if it isn't available.
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
 * Gets the value of [property] or `null`` if it isn't available.
 */
fun Temporal.getOrNull(property: BooleanProperty): Boolean? {
    return if (has(property)) get(property) else null
}

/**
 * Gets the value of [property] or `null`` if it isn't available.
 */
fun Temporal.getOrNull(property: NumberProperty): Long? {
    return if (has(property)) get(property) else null
}

/**
 * Gets the value of [property] or `null`` if it isn't available.
 */
fun <T> Temporal.getOrNull(property: ObjectProperty<T>): T? {
    return if (has(property)) get(property) else null
}

/**
 * Gets the value of [property] within a given [context].
 * @throws TemporalPropertyException if the property isn't available
 */
fun <Context> Temporal.get(property: ContextualNumberProperty<Context>, context: Context): Long {
    return property.deriveValueFrom(this, context)
}

/**
 * Gets the value of [property] within a given [context] or the result of [defaultValue] if it isn't available.
 */
inline fun <Context> Temporal.getOrElse(
    property: ContextualNumberProperty<Context>,
    context: Context,
    defaultValue: (property: ContextualNumberProperty<Context>) -> Long
): Long {
    return if (property.isDerivableFrom(this)) {
        get(property, context)
    } else {
        defaultValue(property)
    }
}

/**
 * Gets the value of [property] within a given [context] or `null`` if it isn't available.
 */
fun <Context> Temporal.getOrNull(property: ContextualNumberProperty<Context>, context: Context): Long? {
    return if (property.isDerivableFrom(this)) {
        get(property, context)
    } else {
        null
    }
}

internal fun throwUnsupportedTemporalPropertyException(property: TemporalProperty<*>): Nothing {
    throw TemporalPropertyException("'$property' is not supported")
}
