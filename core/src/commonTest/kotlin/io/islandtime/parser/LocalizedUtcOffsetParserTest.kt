package io.islandtime.parser

import io.islandtime.base.TemporalProperty
import io.islandtime.locale.toLocale
import io.islandtime.properties.UtcOffsetProperty
import io.islandtime.test.AbstractIslandTimeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

@Suppress("PrivatePropertyName")
class LocalizedUtcOffsetParserTest : AbstractIslandTimeTest() {
    private val en_US = "en-US".toLocale()
    private val fr_FR = "fr-FR".toLocale()

    @Test
    fun `parses short GMT format`() {
        val baseParser = TemporalParser { localizedOffset() }

        listOf(en_US, fr_FR).forEach { locale ->
            listOf(
                "GMT" to listOf(UtcOffsetProperty.TotalSeconds to 0L),
                "GMT+1" to listOf(UtcOffsetProperty.Sign to 1L, UtcOffsetProperty.Hours to 1L),
                "GMT-1" to listOf(UtcOffsetProperty.Sign to -1L, UtcOffsetProperty.Hours to 1L),
                "GMT+10" to listOf(UtcOffsetProperty.Sign to 1L, UtcOffsetProperty.Hours to 10L),
                "GMT-10" to listOf(UtcOffsetProperty.Sign to -1L, UtcOffsetProperty.Hours to 10L),
                "GMT+18" to listOf(UtcOffsetProperty.Sign to 1L, UtcOffsetProperty.Hours to 18L),
                "GMT-18" to listOf(UtcOffsetProperty.Sign to -1L, UtcOffsetProperty.Hours to 18L),
                "GMT+01" to listOf(UtcOffsetProperty.Sign to 1L, UtcOffsetProperty.Hours to 1L),
                "GMT-09" to listOf(UtcOffsetProperty.Sign to -1L, UtcOffsetProperty.Hours to 9L),
                "GMT+1:30" to listOf(
                    UtcOffsetProperty.Sign to 1L,
                    UtcOffsetProperty.Hours to 1L,
                    UtcOffsetProperty.Minutes to 30L
                ),
                "GMT-1:30" to listOf(
                    UtcOffsetProperty.Sign to -1L,
                    UtcOffsetProperty.Hours to 1L,
                    UtcOffsetProperty.Minutes to 30L
                ),
                "GMT+19:60" to listOf(
                    UtcOffsetProperty.Sign to 1L,
                    UtcOffsetProperty.Hours to 19L,
                    UtcOffsetProperty.Minutes to 60L
                ),
                "GMT-19:60" to listOf(
                    UtcOffsetProperty.Sign to -1L,
                    UtcOffsetProperty.Hours to 19L,
                    UtcOffsetProperty.Minutes to 60L
                ),
                "GMT+1:30:10" to listOf(
                    UtcOffsetProperty.Sign to 1L,
                    UtcOffsetProperty.Hours to 1L,
                    UtcOffsetProperty.Minutes to 30L,
                    UtcOffsetProperty.Seconds to 10L
                ),
                "GMT-1:30:10" to listOf(
                    UtcOffsetProperty.Sign to -1L,
                    UtcOffsetProperty.Hours to 1L,
                    UtcOffsetProperty.Minutes to 30L,
                    UtcOffsetProperty.Seconds to 10L
                ),
                "GMT+10:30:10" to listOf(
                    UtcOffsetProperty.Sign to 1L,
                    UtcOffsetProperty.Hours to 10L,
                    UtcOffsetProperty.Minutes to 30L,
                    UtcOffsetProperty.Seconds to 10L
                ),
                "GMT-10:30:10" to listOf(
                    UtcOffsetProperty.Sign to -1L,
                    UtcOffsetProperty.Hours to 10L,
                    UtcOffsetProperty.Minutes to 30L,
                    UtcOffsetProperty.Seconds to 10L
                ),
            ).forEach { (baseText, properties) ->
                val expectedResult = properties.toMap<TemporalProperty<*>, Any>()

                listOf("", " ", "C", ":", "-", "+", "+C").forEach { endChars ->
                    val text = "$baseText$endChars"

                    val parser = TemporalParser {
                        use(baseParser)
                        +endChars
                    }

                    val result = try {
                        parser.parse(text, TemporalParser.Settings(locale = locale))
                    } catch (e: TemporalParseException) {
                        fail(message = "'$text' failed with $e")
                    }

                    assertEquals(expectedResult, result.properties, message = text)
                }
            }
        }
    }

    @Test
    fun `fails to parse invalid short formats`() {
        val baseParser = TemporalParser { localizedOffset() }

        listOf(en_US, fr_FR).forEach { locale ->
            listOf(
                "",
                "gmt",
                "GM",
                "UTC",
                "GM+1",
                "GMT1",
                "GMT4"
            ).forEach { baseText ->
                listOf("", " ", "C", ":", "-", "+", "+C").forEach { endChars ->
                    val text = "$baseText$endChars"

                    val parser = TemporalParser {
                        use(baseParser)
                        +endChars
                    }

                    assertFailsWith<TemporalParseException>(text) {
                        parser.parse(text, TemporalParser.Settings(locale = locale))
                    }
                }
            }
        }
    }

    @Test
    fun `parses long GMT format`() {
        val baseParser = TemporalParser { localizedOffset(longFormatOnly = true) }

        listOf(en_US, fr_FR).forEach { locale ->
            listOf(
                "GMT" to listOf(UtcOffsetProperty.TotalSeconds to 0L),
                "GMT+19:60" to listOf(
                    UtcOffsetProperty.Sign to 1L,
                    UtcOffsetProperty.Hours to 19L,
                    UtcOffsetProperty.Minutes to 60L
                ),
                "GMT-19:60" to listOf(
                    UtcOffsetProperty.Sign to -1L,
                    UtcOffsetProperty.Hours to 19L,
                    UtcOffsetProperty.Minutes to 60L
                ),
                "GMT+10:30:10" to listOf(
                    UtcOffsetProperty.Sign to 1L,
                    UtcOffsetProperty.Hours to 10L,
                    UtcOffsetProperty.Minutes to 30L,
                    UtcOffsetProperty.Seconds to 10L
                ),
                "GMT-10:30:10" to listOf(
                    UtcOffsetProperty.Sign to -1L,
                    UtcOffsetProperty.Hours to 10L,
                    UtcOffsetProperty.Minutes to 30L,
                    UtcOffsetProperty.Seconds to 10L
                ),
            ).forEach { (baseText, properties) ->
                val expectedResult = properties.toMap<TemporalProperty<*>, Any>()

                listOf("", " ", "C", ":", ":C", ":CC", "-", "+", "+C", "+1C", "+CC").forEach { endChars ->
                    val text = "$baseText$endChars"

                    val parser = TemporalParser {
                        use(baseParser)
                        +endChars
                    }

                    val result = try {
                        parser.parse(text, TemporalParser.Settings(locale = locale))
                    } catch (e: TemporalParseException) {
                        fail(message = "'$text' failed with $e")
                    }

                    assertEquals(expectedResult, result.properties, message = text)
                }
            }
        }
    }

    @Test
    fun `fails to parse invalid long formats`() {
        val baseParser = TemporalParser { localizedOffset(longFormatOnly = true) }

        listOf(en_US, fr_FR).forEach { locale ->
            listOf(
                "",
                "gmt",
                "GM",
                "UTC",
                "GMT+1",
                "GMT-1",
                "GMT+01",
                "GMT-01",
                "GMT+10",
                "GMT-10",
                "GMT-09:3",
                "GMT+1:30",
                "GMT-5:00"
            ).forEach { baseText ->
                listOf("", " ", "C", ":", "-", "+", "+C").forEach { endChars ->
                    val text = "$baseText$endChars"

                    val parser = TemporalParser {
                        use(baseParser)
                        +endChars
                    }

                    assertFailsWith<TemporalParseException>(text) {
                        parser.parse(text, TemporalParser.Settings(locale = locale))
                    }
                }
            }
        }
    }

    @Test
    fun `respects case sensitivity`() {
        val parser = TemporalParser {
            caseInsensitive { localizedOffset() }
        }

        assertEquals(
            mapOf<TemporalProperty<*>, Any>(UtcOffsetProperty.TotalSeconds to 0L),
            parser.parse("gmt", TemporalParser.Settings(locale = en_US)).properties
        )
    }
}
