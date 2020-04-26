package io.islandtime.test

import io.islandtime.base.*

fun temporalWith(vararg properties: Pair<TemporalProperty<*>, Any>): Temporal {
    return TestTemporal(properties.toMap())
}

private class TestTemporal(private val map: Map<TemporalProperty<*>, Any>) : Temporal {
    override fun has(property: TemporalProperty<*>): Boolean {
        return map.containsKey(property)
    }

    override fun get(property: NumberProperty): Long {
        return when (val value = map[property]) {
            is Int -> value.toLong()
            is Long -> value
            else -> super.get(property)
        }
    }

    override fun get(property: BooleanProperty): Boolean {
        return when (val value = map[property]) {
            is Boolean -> value
            else -> super.get(property)
        }
    }

    override fun <T> get(property: ObjectProperty<T>): T {
        @Suppress("UNCHECKED_CAST")
        return map[property] as? T ?: super.get(property)
    }
}