package io.islandtime.operators

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.measures.TimeUnit.*
import kotlin.jvm.JvmName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RoundUpTest {
    private val testOffset = (-5).hours.asUtcOffset()
    private val nyZone = TimeZone("America/New_York")

    @Test
    fun `throws an exception when an hour increment is out of range`() {
        testException(
            listOf(
                (-1).hours,
                0.hours,
                25.hours,
                48.hours,
                Int.MIN_VALUE.hours,
                Int.MAX_VALUE.hours
            )
        )
    }

    @Test
    fun `throws an exception when an hour increment can't be divided out evenly`() {
        testException(
            listOf(
                5.hours,
                7.hours,
                13.hours,
                18.hours
            )
        )
    }

    @Test
    fun `round up to nearest 2-hours`() {
        testRoundedUpToNearest(
            2.hours,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:00" to "1969-12-31T02:00",
            "1969-12-31T00:59:59.999999999" to "1969-12-31T02:00",
            "1969-12-31T00:00:00.000000001" to "1969-12-31T02:00",
            "1969-12-30T23:59:59.999999999" to "1969-12-31T00:00",
            "1969-12-31T23:00" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `throws an exception when a minute increment is out of range`() {
        testException(
            listOf(
                (-1).minutes,
                0.minutes,
                61.minutes,
                Int.MIN_VALUE.minutes,
                Int.MAX_VALUE.minutes
            )
        )
    }

    @Test
    fun `throws an exception when a minute increment can't be divided out evenly`() {
        testException(
            listOf(
                7.minutes,
                11.minutes,
                45.minutes
            )
        )
    }

    @Test
    fun `round up to nearest 2-minutes`() {
        testRoundedUpToNearest(
            2.minutes,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:59" to "1969-12-31T02:00",
            "1969-12-31T02:01:01.9" to "1969-12-31T02:02",
            "1969-12-31T23:59:59.999999999" to "1970-01-01T00:00",
            "1969-12-31T23:59:00" to "1970-01-01T00:00",
            "1970-01-01T00:01:59" to "1970-01-01T00:02"
        )
    }

    @Test
    fun `round up to nearest 30-minutes`() {
        testRoundedUpToNearest(
            30.minutes,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:15:00.000000001" to "1969-12-31T01:30",
            "1969-12-31T01:14:59.999" to "1969-12-31T01:30",
            "1969-12-31T23:45:00.000000001" to "1970-01-01T00:00",
            "1970-01-01T00:15:30" to "1970-01-01T00:30",
            "1970-01-01T00:14:59.999999999" to "1970-01-01T00:30"
        )
    }

    @Test
    fun `throws an exception when a second increment is out of range`() {
        testException(
            listOf(
                (-1).seconds,
                0.seconds,
                61.seconds,
                Int.MIN_VALUE.seconds,
                Int.MAX_VALUE.seconds
            )
        )
    }

    @Test
    fun `throws an exception when a second increment can't be divided out evenly`() {
        testException(listOf(7.seconds, 11.seconds, 13.seconds))
    }

    @Test
    fun `round up to nearest 2-seconds`() {
        testRoundedUpToNearest(
            2.seconds,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:59:01" to "1969-12-31T01:59:02",
            "1969-12-31T02:01:01.9" to "1969-12-31T02:01:02",
            "1969-12-31T23:59:59.999999999" to "1970-01-01T00:00",
            "1969-12-31T23:59:59" to "1970-01-01T00:00",
            "1970-01-01T00:01:59" to "1970-01-01T00:02"
        )
    }

    @Test
    fun `throws an exception when a millisecond increment is out of range`() {
        testException(
            listOf(
                (-1).milliseconds,
                0.milliseconds,
                (1.seconds + 1.milliseconds).toIntMilliseconds(),
                Int.MIN_VALUE.milliseconds,
                Int.MAX_VALUE.milliseconds
            )
        )
    }

    @Test
    fun `throws an exception when a millisecond increment can't be divided out evenly`() {
        testException(
            listOf(
                7.milliseconds,
                11.milliseconds,
                13.milliseconds,
                600.milliseconds
            )
        )
    }

    @Test
    fun `round up to nearest 2-milliseconds`() {
        testRoundedUpToNearest(
            2.milliseconds,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:59:01.999" to "1969-12-31T01:59:02",
            "1969-12-31T02:00:01.001" to "1969-12-31T02:00:01.002",
            "1969-12-31T23:59:59.99900349" to "1970-01-01T00:00",
            "1969-12-31T23:59:59.999" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `round up to nearest 100-milliseconds`() {
        testRoundedUpToNearest(
            100.milliseconds,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:59:01.95" to "1969-12-31T01:59:02",
            "1969-12-31T01:59:01.949999999" to "1969-12-31T01:59:02",
            "1969-12-31T23:59:59.951" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `round up to nearest 1000-milliseconds`() {
        testRoundedUpToNearest(
            1_000.milliseconds,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:59:01.95" to "1969-12-31T01:59:02",
            "1969-12-31T01:59:01.949999999" to "1969-12-31T01:59:02",
            "1969-12-31T23:59:59.951" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `throws an exception when a microsecond increment is out of range`() {
        testException(
            listOf(
                (-1).microseconds,
                0.microseconds,
                1_000_001.microseconds,
                Int.MIN_VALUE.microseconds,
                Int.MAX_VALUE.microseconds
            )
        )
    }

    @Test
    fun `throws an exception when a microsecond increment can't be divided out evenly`() {
        testException(
            listOf(
                7.microseconds,
                11.microseconds,
                13.microseconds,
                600_000.microseconds
            )
        )
    }

    @Test
    fun `round up to nearest 100-microseconds`() {
        testRoundedUpToNearest(
            100.microseconds,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:59:01.99995" to "1969-12-31T01:59:02",
            "1969-12-31T01:59:01.999949999" to "1969-12-31T01:59:02",
            "1969-12-31T02:00:01.000001" to "1969-12-31T02:00:01.0001",
            "1969-12-31T23:59:59.999951" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `throws an exception when a nanosecond increment is out of range`() {
        testException(
            listOf(
                (-1).nanoseconds,
                0.nanoseconds,
                1_000_000_001.nanoseconds,
                Int.MIN_VALUE.nanoseconds,
                Int.MAX_VALUE.nanoseconds
            )
        )
    }

    @Test
    fun `throws an exception when a nanosecond increment can't be divided out evenly`() {
        testException(
            listOf(
                7.nanoseconds,
                11.nanoseconds,
                13.nanoseconds,
                600_000_000.nanoseconds
            )
        )
    }

    @Test
    fun `round up to nearest 2-nanoseconds`() {
        testRoundedUpToNearest(
            2.nanoseconds,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T01:59:01.999999999" to "1969-12-31T01:59:02",
            "1969-12-31T01:59:01.999999998" to "1969-12-31T01:59:01.999999998"
        )
    }

    @Test
    fun `round up to days`() {
        testRoundedUpTo(
            DAYS,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T12:00" to "1970-01-01T00:00",
            "1969-12-31T00:00:00.000000001" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `round up to hours`() {
        testRoundedUpTo(
            HOURS,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T23:30" to "1970-01-01T00:00",
            "1969-12-31T23:00:00.000000001" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `round up to minutes`() {
        testRoundedUpTo(
            MINUTES,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T23:59:30" to "1970-01-01T00:00",
            "1969-12-31T23:59:00.000000001" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `round up to seconds`() {
        testRoundedUpTo(
            SECONDS,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T23:59:59.5" to "1970-01-01T00:00",
            "1969-12-31T23:59:59.000000001" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `round up to milliseconds`() {
        testRoundedUpTo(
            MILLISECONDS,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T23:59:59.9995" to "1970-01-01T00:00",
            "1969-12-31T23:59:59.999499999" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `round up to microseconds`() {
        testRoundedUpTo(
            MICROSECONDS,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T23:59:59.9999995" to "1970-01-01T00:00",
            "1969-12-31T23:59:59.999999499" to "1970-01-01T00:00"
        )
    }

    @Test
    fun `rounding to nanoseconds does nothing`() {
        testRoundedUpTo(
            NANOSECONDS,
            "1970-01-01T00:00" to "1970-01-01T00:00",
            "1969-12-31T23:59:59.999999999" to "1969-12-31T23:59:59.999999999"
        )
    }

    @JvmName("testExceptionHours")
    private fun testException(invalidIncrements: List<IntHours>) {
        invalidIncrements.forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(nyZone).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedUpToNearest(it)
            }
        }
    }

    @JvmName("testExceptionMinutes")
    private fun testException(invalidIncrements: List<IntMinutes>) {
        invalidIncrements.forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(nyZone).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedUpToNearest(it)
            }
        }
    }

    @JvmName("testExceptionSeconds")
    private fun testException(invalidIncrements: List<IntSeconds>) {
        invalidIncrements.forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(nyZone).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedUpToNearest(it)
            }
        }
    }

    @JvmName("testExceptionMilliseconds")
    private fun testException(invalidIncrements: List<IntMilliseconds>) {
        invalidIncrements.forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(nyZone).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedUpToNearest(it)
            }
        }
    }

    @JvmName("testExceptionMicroseconds")
    private fun testException(invalidIncrements: List<IntMicroseconds>) {
        invalidIncrements.forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(nyZone).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedUpToNearest(it)
            }
        }
    }

    @JvmName("testExceptionNanoseconds")
    private fun testException(invalidIncrements: List<IntNanoseconds>) {
        invalidIncrements.forEach {
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Time.MIDNIGHT.at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(testOffset).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                DateTime(2012, 5, 5, 0, 0).at(nyZone).roundedUpToNearest(it)
            }
            assertFailsWith<IllegalArgumentException>("Failed at '$it'") {
                Instant.UNIX_EPOCH.roundedUpToNearest(it)
            }
        }
    }

//    private val testObjects = listOf(
//        Time.MIDNIGHT,
//        Time.MIDNIGHT.at(testOffset),
//        DateTime(2012, 5, 5, 0, 0),
//        DateTime(2012, 5, 5, 0, 0).at(testOffset),
//        DateTime(2012, 5, 5, 0, 0).at(nyZone),
//        Instant.UNIX_EPOCH
//    )
//
//    private val testOperations = listOf<Any.(IntHours) -> Any>(
//        Time::roundedUpToNearest,
//        OffsetTime::roundedUpToNearest,
//        DateTime::roundedUpToNearest,
//        OffsetDateTime::roundedUpToNearest,
//        ZonedDateTime::roundedUpToNearest,
//        Instant::roundedUpToNearest
//    )
//
//    private inline fun <T, U> testException(objs: List<T>, quantities: List<U>, operation: T.(U) -> T) {
//        for (obj in objs) {
//            for (quantity in quantities) {
//                assertFailsWith<IllegalArgumentException>("Failed at '$obj'") {
//                    obj.operation(quantity)
//                }
//            }
//        }
//    }

    private fun testRoundedUpTo(unit: TimeUnit, vararg dateTimeValues: Pair<String, String>) {
        dateTimeValues.map { (input, output) -> input.toDateTime() to output.toDateTime() }
            .forEach { (inDateTime, outDateTime) ->
                assertEquals(outDateTime, inDateTime.roundedUpTo(unit))
                assertEquals(outDateTime at testOffset, inDateTime.at(testOffset).roundedUpTo(unit))
                assertEquals(outDateTime at nyZone, inDateTime.at(nyZone).roundedUpTo(unit))
                assertEquals(outDateTime.time, inDateTime.time.roundedUpTo(unit))
                assertEquals(outDateTime.time at testOffset, inDateTime.time.at(testOffset).roundedUpTo(unit))

                assertEquals(
                    outDateTime.toInstantAt(UtcOffset.ZERO),
                    inDateTime.toInstantAt(UtcOffset.ZERO).roundedUpTo(unit)
                )
            }
    }

    private fun testRoundedUpToNearest(increment: IntHours, vararg dateTimeValues: Pair<String, String>) {
        dateTimeValues.map { (input, output) -> input.toDateTime() to output.toDateTime() }
            .forEach { (inDateTime, outDateTime) ->
                assertEquals(outDateTime, inDateTime.roundedUpToNearest(increment))
                assertEquals(outDateTime at testOffset, inDateTime.at(testOffset).roundedUpToNearest(increment))
                assertEquals(outDateTime at nyZone, inDateTime.at(nyZone).roundedUpToNearest(increment))
                assertEquals(outDateTime.time, inDateTime.time.roundedUpToNearest(increment))

                assertEquals(
                    outDateTime.time at testOffset,
                    inDateTime.time.at(testOffset).roundedUpToNearest(increment)
                )

                assertEquals(
                    outDateTime.toInstantAt(UtcOffset.ZERO),
                    inDateTime.toInstantAt(UtcOffset.ZERO).roundedUpToNearest(increment)
                )
            }
    }

    private fun testRoundedUpToNearest(increment: IntMinutes, vararg dateTimeValues: Pair<String, String>) {
        dateTimeValues.map { (input, output) -> input.toDateTime() to output.toDateTime() }
            .forEach { (inDateTime, outDateTime) ->
                assertEquals(outDateTime, inDateTime.roundedUpToNearest(increment))
                assertEquals(outDateTime at testOffset, inDateTime.at(testOffset).roundedUpToNearest(increment))
                assertEquals(outDateTime at nyZone, inDateTime.at(nyZone).roundedUpToNearest(increment))
                assertEquals(outDateTime.time, inDateTime.time.roundedUpToNearest(increment))

                assertEquals(
                    outDateTime.time at testOffset,
                    inDateTime.time.at(testOffset).roundedUpToNearest(increment)
                )

                assertEquals(
                    outDateTime.toInstantAt(UtcOffset.ZERO),
                    inDateTime.toInstantAt(UtcOffset.ZERO).roundedUpToNearest(increment)
                )
            }
    }

    private fun testRoundedUpToNearest(increment: IntSeconds, vararg dateTimeValues: Pair<String, String>) {
        dateTimeValues.map { (input, output) -> input.toDateTime() to output.toDateTime() }
            .forEach { (inDateTime, outDateTime) ->
                assertEquals(outDateTime, inDateTime.roundedUpToNearest(increment))
                assertEquals(outDateTime at testOffset, inDateTime.at(testOffset).roundedUpToNearest(increment))
                assertEquals(outDateTime at nyZone, inDateTime.at(nyZone).roundedUpToNearest(increment))
                assertEquals(outDateTime.time, inDateTime.time.roundedUpToNearest(increment))

                assertEquals(
                    outDateTime.time at testOffset,
                    inDateTime.time.at(testOffset).roundedUpToNearest(increment)
                )

                assertEquals(
                    outDateTime.toInstantAt(UtcOffset.ZERO),
                    inDateTime.toInstantAt(UtcOffset.ZERO).roundedUpToNearest(increment)
                )
            }
    }

    private fun testRoundedUpToNearest(increment: IntMilliseconds, vararg dateTimeValues: Pair<String, String>) {
        dateTimeValues.map { (input, output) -> input.toDateTime() to output.toDateTime() }
            .forEach { (inDateTime, outDateTime) ->
                assertEquals(outDateTime, inDateTime.roundedUpToNearest(increment))
                assertEquals(outDateTime at testOffset, inDateTime.at(testOffset).roundedUpToNearest(increment))
                assertEquals(outDateTime at nyZone, inDateTime.at(nyZone).roundedUpToNearest(increment))
                assertEquals(outDateTime.time, inDateTime.time.roundedUpToNearest(increment))

                assertEquals(
                    outDateTime.time at testOffset,
                    inDateTime.time.at(testOffset).roundedUpToNearest(increment)
                )

                assertEquals(
                    outDateTime.toInstantAt(UtcOffset.ZERO),
                    inDateTime.toInstantAt(UtcOffset.ZERO).roundedUpToNearest(increment)
                )
            }
    }

    private fun testRoundedUpToNearest(increment: IntMicroseconds, vararg dateTimeValues: Pair<String, String>) {
        dateTimeValues.map { (input, output) -> input.toDateTime() to output.toDateTime() }
            .forEach { (inDateTime, outDateTime) ->
                assertEquals(outDateTime, inDateTime.roundedUpToNearest(increment))
                assertEquals(outDateTime at testOffset, inDateTime.at(testOffset).roundedUpToNearest(increment))
                assertEquals(outDateTime at nyZone, inDateTime.at(nyZone).roundedUpToNearest(increment))
                assertEquals(outDateTime.time, inDateTime.time.roundedUpToNearest(increment))

                assertEquals(
                    outDateTime.time at testOffset,
                    inDateTime.time.at(testOffset).roundedUpToNearest(increment)
                )

                assertEquals(
                    outDateTime.toInstantAt(UtcOffset.ZERO),
                    inDateTime.toInstantAt(UtcOffset.ZERO).roundedUpToNearest(increment)
                )
            }
    }

    private fun testRoundedUpToNearest(increment: IntNanoseconds, vararg dateTimeValues: Pair<String, String>) {
        dateTimeValues.map { (input, output) -> input.toDateTime() to output.toDateTime() }
            .forEach { (inDateTime, outDateTime) ->
                assertEquals(outDateTime, inDateTime.roundedUpToNearest(increment))
                assertEquals(outDateTime at testOffset, inDateTime.at(testOffset).roundedUpToNearest(increment))
                assertEquals(outDateTime at nyZone, inDateTime.at(nyZone).roundedUpToNearest(increment))
                assertEquals(outDateTime.time, inDateTime.time.roundedUpToNearest(increment))

                assertEquals(
                    outDateTime.time at testOffset,
                    inDateTime.time.at(testOffset).roundedUpToNearest(increment)
                )

                assertEquals(
                    outDateTime.toInstantAt(UtcOffset.ZERO),
                    inDateTime.toInstantAt(UtcOffset.ZERO).roundedUpToNearest(increment)
                )
            }
    }
}