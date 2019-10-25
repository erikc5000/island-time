package io.islandtime.extensions.kotlintime

import io.islandtime.measures.durationOf
import io.islandtime.measures.nanoseconds
import io.islandtime.measures.seconds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.todo
import kotlin.time.seconds as kotlinSeconds
import kotlin.time.nanoseconds as kotlinNanoseconds

@UseExperimental(kotlin.time.ExperimentalTime::class)
class ConversionsTest {
    @Test
    fun `converts Kotlin Duration to Island Duration`() {
        assertEquals(durationOf(1.seconds, 1.nanoseconds), (1.kotlinSeconds + 1.kotlinNanoseconds).toIslandDuration())

        assertEquals(
            durationOf((-1).seconds, (-1).nanoseconds),
            ((-1).kotlinSeconds - 1.kotlinNanoseconds).toIslandDuration()
        )
    }

    @Test
    fun `converts Island Duration to Kotlin Duration`() {
        // Comparison requires tolerance
        todo {
            assertEquals(
                (1.kotlinSeconds + 1.kotlinNanoseconds),
                durationOf(1.seconds, 1.nanoseconds).toKotlinDuration()
            )

            assertEquals(
                ((-1).kotlinSeconds - 1.kotlinNanoseconds),
                durationOf((-1).seconds, (-1).nanoseconds).toKotlinDuration()
            )
        }
    }
}