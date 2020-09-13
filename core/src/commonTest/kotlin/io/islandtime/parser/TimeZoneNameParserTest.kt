package io.islandtime.parser

import io.islandtime.base.TemporalProperty
import io.islandtime.format.TimeZoneNameStyle
import io.islandtime.locale.toLocale
import io.islandtime.parser.dsl.DisambiguationStrategy
import io.islandtime.parser.dsl.disambiguate
import io.islandtime.properties.TimeZoneProperty
import io.islandtime.properties.UtcOffsetProperty
import io.islandtime.test.AbstractIslandTimeTest
import io.islandtime.test.FakeTimeZoneNameProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Suppress("PrivatePropertyName")
class TimeZoneNameParserTest : AbstractIslandTimeTest(testTimeZoneNameProvider = FakeTimeZoneNameProvider) {
    private val en_US = "en-US".toLocale()

    @Test
    fun `throws an exception when no styles are specified`() {
        assertFailsWith<IllegalArgumentException> {
            TemporalParser {
                timeZoneName { styles = emptySet() }
            }
        }
    }

    @Test
    fun `returns an error if there are no characters left to parse`() {
        assertFailsWith<TemporalParseException> {
            TemporalParser { timeZoneName() }.parse("")
        }

        val exception = assertFailsWith<TemporalParseException> {
            TemporalParser {
                +"Some text"
                timeZoneName()
            }.parse("Some text")
        }
        assertEquals(9, exception.errorIndex)
    }

    @Test
    fun `picks the first alphabetical zone ID by default when multiple zones have the same name`() {
        val parser = TemporalParser {
            timeZoneName {
                styles = setOf(TimeZoneNameStyle.LONG_STANDARD)
            }
        }

        val result = parser.parse("Eastern Standard Time", TemporalParser.Settings(locale = en_US))
        assertEquals(1, result.propertyCount)
        assertEquals("America/Detroit", result[TimeZoneProperty.Id])
    }

    @Test
    fun `parses known zone IDs in addition to names`() {
        val parser = TemporalParser {
            timeZoneName {
                styles = setOf(TimeZoneNameStyle.LONG_STANDARD)
            }
        }

        val result = parser.parse("America/Denver", TemporalParser.Settings(locale = en_US))
        assertEquals(1, result.propertyCount)
        assertEquals("America/Denver", result[TimeZoneProperty.Id])
    }

    @Test
    fun `fails parsing when a matching name can't be found`() {
        val parser = TemporalParser {
            timeZoneName {
                styles = setOf(TimeZoneNameStyle.LONG_STANDARD)
            }
        }

        val exception = assertFailsWith<TemporalParseException> {
            parser.parse("Nonexistent Time", TemporalParser.Settings(locale = en_US))
        }
        assertEquals(0, exception.errorIndex)
    }

    @Test
    fun `DisambiguationStrategy_RAISE_ERROR fails parsing when multiple zones have the same name`() {
        val parser = TemporalParser {
            timeZoneName {
                styles = setOf(TimeZoneNameStyle.LONG_STANDARD)
                disambiguate(DisambiguationStrategy.RAISE_ERROR)
            }
        }

        assertFailsWith<TemporalParseException> {
            parser.parse("Eastern Standard Time", TemporalParser.Settings(locale = en_US))
        }
    }

    @Test
    fun `DisambiguationStrategy_RAISE_ERROR succeeds when the zone name is unique`() {
        val parser = TemporalParser {
            timeZoneName {
                styles = setOf(TimeZoneNameStyle.LONG_GENERIC)
                disambiguate(DisambiguationStrategy.RAISE_ERROR)
            }
        }

        val result = parser.parse("Mountain Time", TemporalParser.Settings(locale = en_US))
        assertEquals(1, result.propertyCount)
        assertEquals("America/Denver", result[TimeZoneProperty.Id])
    }

    @Test
    fun `custom disambiguation action can select from ambiguous zone IDs`() {
        val parser = TemporalParser {
            timeZoneName {
                styles = setOf(TimeZoneNameStyle.LONG_STANDARD)

                disambiguate { _, possibleValues ->
                    val preferredZones = listOf("America/New_York")
                    preferredZones.firstOrNull { it in possibleValues } ?: possibleValues.first()
                }
            }
        }

        val result = parser.parse("Eastern Standard Time", TemporalParser.Settings(locale = en_US))
        assertEquals(1, result.propertyCount)
        assertEquals("America/New_York", result[TimeZoneProperty.Id])
    }

    @Test
    fun `respects the case sensitivity setting`() {
        val parser = TemporalParser {
            caseInsensitive {
                timeZoneName {
                    styles = setOf(TimeZoneNameStyle.LONG_STANDARD)
                }
            }
        }

        val result = parser.parse("eastern standard time", TemporalParser.Settings(locale = en_US))
        assertEquals(1, result.propertyCount)
        assertEquals("America/Detroit", result[TimeZoneProperty.Id])
    }

    @Test
    fun `parses localized offsets when a match can't be found or the GMT string was matched`() {
        val parser = TemporalParser {
            timeZoneName {
                styles = setOf(TimeZoneNameStyle.SHORT_GENERIC)
            }
        }

        val result = parser.parse("GMT+1", TemporalParser.Settings(locale = en_US))

        assertEquals(
            mapOf<TemporalProperty<*>, Any>(UtcOffsetProperty.Sign to 1L, UtcOffsetProperty.Hours to 1L),
            result.properties
        )
    }
}
