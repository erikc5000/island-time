package io.islandtime.base

import kotlinx.atomicfu.atomic

abstract class ProviderProxy<T : Any> {
    @Suppress("ObjectPropertyName")
    private val _provider = atomic<T?>(null)

    protected var provider: T
        set(value) {
            _provider.value = value
        }
        get() = _provider.value ?: run {
            _provider.compareAndSet(null, createDefault())
            checkNotNull(_provider.value) { "Failed to initialize $this" }
        }

    fun set(provider: T) {
        this.provider = provider
    }

    fun reset() {
        _provider.value = null
    }

    protected abstract fun createDefault(): T
}
