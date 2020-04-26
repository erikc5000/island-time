package io.islandtime.format

import io.islandtime.base.TemporalPropertyException
import io.islandtime.base.TimeZoneProperty
import io.islandtime.base.UtcOffsetProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.temporalWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SignFormatterTest : AbstractIslandTimeTest() {
    private val signFormatter = temporalFormatter { sign(UtcOffsetProperty.Sign) }

    @Test
    fun `throws an exception when given an invalid property`() {
        val temporal = temporalWith(TimeZoneProperty.Id to "Nothing")
        assertFailsWith<TemporalPropertyException> { signFormatter.format(temporal) }
    }

    @Test
    fun `respects the number style's plus sign`() {
        val temporal = temporalWith(UtcOffsetProperty.Sign to 0)
        val numberStyle = NumberStyle.DEFAULT.copy(plusSign = listOf('#', '!'))

        assertEquals(
            "#",
            signFormatter.format(temporal, TemporalFormatter.Settings(numberStyle = numberStyle))
        )
    }

    @Test
    fun `respects the number style's minus sign`() {
        val temporal = temporalWith(UtcOffsetProperty.Sign to -1)
        val numberStyle = NumberStyle.DEFAULT.copy(minusSign = listOf('#', '!'))

        assertEquals(
            "#",
            signFormatter.format(temporal, TemporalFormatter.Settings(numberStyle = numberStyle))
        )
    }
}