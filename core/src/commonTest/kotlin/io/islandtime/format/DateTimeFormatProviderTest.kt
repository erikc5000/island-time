package io.islandtime.format

import io.islandtime.locale.localeOf
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DateTimeFormatProviderTest : AbstractIslandTimeTest() {
    @Test
    fun `throws an exception when date and time style are both null`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatProvider.formatterFor(null, null, localeOf("en-US"))
        }
    }
}