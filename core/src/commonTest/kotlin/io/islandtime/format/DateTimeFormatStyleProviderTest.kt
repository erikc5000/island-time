package io.islandtime.format

import kotlin.test.Test
import kotlin.test.assertFailsWith

class DateTimeFormatStyleProviderTest {
    @Test
    fun `throws an exception when date and time style are both null`() {
        assertFailsWith<IllegalArgumentException> {
            DateTimeFormatStyleProvider.formatterFor(null, null)
        }
    }
}