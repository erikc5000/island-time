package io.islandtime.format

import io.islandtime.base.DateProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals

class OnlyIfFormatterTest : AbstractIslandTimeTest() {
    @Test
    fun `empty onlyIf() does nothing on temporal formatter`() {
        val formatter = temporalFormatter { onlyIf({ true }) {} }

        assertEquals(
            "",
            formatter.format(temporalWith(DateProperty.Year to 2000))
        )
    }

    @Test
    fun `conditionally executes block on temporal formatter`() {
        val formatter = temporalFormatter {
            onlyIf({ it.has(DateProperty.Year) }) {
                +"I have a year"
            }
        }

        assertEquals(
            "I have a year",
            formatter.format(temporalWith(DateProperty.Year to 2000))
        )

        assertEquals(
            "",
            formatter.format(temporalWith(DateProperty.DayOfMonth to 22))
        )
    }

    @Test
    fun `empty onlyIf() does nothing on date-time formatter`() {
        val formatter = dateTimeFormatter { onlyIf({ true }) {} }

        assertEquals(
            "",
            formatter.format(temporalWith(DateProperty.Year to 2000))
        )
    }

    @Test
    fun `conditionally executes block on date-time formatter`() {
        val formatter = dateTimeFormatter {
            onlyIf({ it.has(DateProperty.Year) }) {
                +"I have a year"
            }
        }

        assertEquals(
            "I have a year",
            formatter.format(temporalWith(DateProperty.Year to 2000))
        )

        assertEquals(
            "",
            formatter.format(temporalWith(DateProperty.DayOfMonth to 22))
        )
    }
}