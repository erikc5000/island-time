package io.islandtime.internal

import kotlin.test.Test
import kotlin.test.assertEquals

class ExtensionsTest {
    @Test
    fun `StringBuilder_appendZeroPadded pads Int as expected`() {
        assertEquals("9", StringBuilder().appendZeroPadded(9, 1).toString())
        assertEquals("0", StringBuilder().appendZeroPadded(0, 1).toString())
        assertEquals("09", StringBuilder().appendZeroPadded(9, 2).toString())
        assertEquals("10", StringBuilder().appendZeroPadded(10, 2).toString())
        assertEquals("0009", StringBuilder().appendZeroPadded(9, 4).toString())
        assertEquals("0060", StringBuilder().appendZeroPadded(60, 4).toString())
    }
}