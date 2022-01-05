package io.islandtime.serialization

import io.islandtime.*
import io.islandtime.measures.*
import io.islandtime.ranges.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class TestData(
    val date: Date,
    val time: Time,
    val dateTime: DateTime,
    val offsetTime: OffsetTime,
    val offsetDateTime: OffsetDateTime,
    val zonedDateTime: ZonedDateTime,
    val yearMonth: YearMonth,
    val year: Year,
    val instant: Instant,
    val zoneRegion: TimeZone,
    val fixedZone: TimeZone,
    val offset: UtcOffset,
    val duration: Duration,
    val period: Period,
    val dateRange: DateRange,
    val dateTimeInterval: DateTimeInterval,
    val instantInterval: InstantInterval,
    val offsetDateTimeInterval: OffsetDateTimeInterval,
    val zonedDateTimeInterval: ZonedDateTimeInterval
)

class SerializationTest {
    private val testData = TestData(
        date = Date(2018, Month.FEBRUARY, 21),
        time = Time(1, 2, 3, 4),
        dateTime = DateTime(2019, Month.FEBRUARY, 21, 1, 2, 3, 4),
        offsetTime = OffsetTime(1, 2, 3, 4, UtcOffset(4.hours, 30.minutes)),
        offsetDateTime = OffsetDateTime(
            2019,
            Month.FEBRUARY,
            21,
            1,
            2,
            3,
            4,
            UtcOffset(4.hours, 30.minutes)
        ),
        zonedDateTime = ZonedDateTime(
            2019,
            Month.FEBRUARY,
            21,
            1,
            2,
            3,
            4,
            TimeZone("America/New_York")
        ),
        yearMonth = YearMonth(2019, Month.FEBRUARY),
        year = Year(2019),
        instant = Instant(1L.seconds, 1.nanoseconds),
        zoneRegion = TimeZone("America/Non_Existent_Zone"),
        fixedZone = TimeZone("+04:30"),
        offset = UtcOffset(4.hours, 30.minutes),
        duration = durationOf(1.seconds, 500.nanoseconds),
        period = periodOf(1.years, 2.months, 3.days),
        dateRange = "2019-01-01/2019-05-31".toDateRange(),
        dateTimeInterval = "2019-01-01T00:00/2019-05-31T12:00".toDateTimeInterval(),
        instantInterval = "2019-01-01T00:00Z/2019-05-31T12:00Z".toInstantInterval(),
        offsetDateTimeInterval = "2019-01-01T00:00+04:30/2019-05-31T12:00+04:30".toOffsetDateTimeInterval(),
        zonedDateTimeInterval = "2019-01-01T00:00-05:00[America/New_York]/2019-05-31T12:00-04:00[America/New_York]"
            .toZonedDateTimeInterval()
    )

    @Test
    fun `serialize and deserialize to json`() {
        val json = Json { prettyPrint = true }
        val text = json.encodeToString(TestData.serializer(), testData)

        assertEquals(
            """
                {
                    "date": "2018-02-21",
                    "time": "01:02:03.000000004",
                    "dateTime": "2019-02-21T01:02:03.000000004",
                    "offsetTime": "01:02:03.000000004+04:30",
                    "offsetDateTime": "2019-02-21T01:02:03.000000004+04:30",
                    "zonedDateTime": "2019-02-21T01:02:03.000000004-05:00[America/New_York]",
                    "yearMonth": "2019-02",
                    "year": "2019",
                    "instant": "1970-01-01T00:00:01.000000001Z",
                    "zoneRegion": "America/Non_Existent_Zone",
                    "fixedZone": "+04:30",
                    "offset": "+04:30",
                    "duration": "PT1.0000005S",
                    "period": "P1Y2M3D",
                    "dateRange": "2019-01-01/2019-05-31",
                    "dateTimeInterval": "2019-01-01T00:00/2019-05-31T12:00",
                    "instantInterval": "2019-01-01T00:00Z/2019-05-31T12:00Z",
                    "offsetDateTimeInterval": "2019-01-01T00:00+04:30/2019-05-31T12:00+04:30",
                    "zonedDateTimeInterval": "2019-01-01T00:00-05:00[America/New_York]/2019-05-31T12:00-04:00[America/New_York]"
                }
            """.trimIndent(),
            text
        )

        val parsedTestData = json.decodeFromString(TestData.serializer(), text)
        assertEquals(testData, parsedTestData)
    }
}
