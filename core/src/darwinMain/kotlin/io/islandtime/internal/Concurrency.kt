package io.islandtime.internal

import kotlinx.cinterop.StableRef
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.ensureNeverFrozen
import kotlin.native.concurrent.freeze

internal class WorkerConfined<T : Any>(
    private val worker: Worker,
    private val value: ConfinedValueRef<T>
) {
    inline fun <R> use(crossinline block: (T) -> R): R {
        return runOn(worker) { block(value.get()) }
    }
}

internal inline class ConfinedValueRef<T : Any>(private val value: StableRef<T>) {
    fun get() = value.get()

    companion object {
        fun <T : Any> create(value: T): ConfinedValueRef<T> {
            return ConfinedValueRef(StableRef.create(value.apply { ensureNeverFrozen() }))
        }
    }
}

internal inline fun <T : Any> Worker.confine(crossinline block: () -> T): WorkerConfined<T> {
    return WorkerConfined(
        this,
        runOn(this) { ConfinedValueRef.create(block()) }
    )
}

private inline fun <T> runOn(worker: Worker, crossinline block: () -> T): T {
    return if (worker == Worker.current) {
        block()
    } else {
        worker.executeImmediately { block() }
    }
}

private fun <T> Worker.executeImmediately(block: () -> T): T {
    return execute(
        TransferMode.SAFE,
        { block.freeze() },
        { runCatching { it() }.freeze() }
    ).result.getOrThrow()
}