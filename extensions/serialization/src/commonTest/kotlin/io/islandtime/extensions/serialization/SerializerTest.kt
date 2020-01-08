package io.islandtime.extensions.serialization

import io.islandtime.*
import io.islandtime.extensions.serialization.measures.DurationSerializer
import io.islandtime.extensions.serialization.measures.PeriodSerializer
import io.islandtime.extensions.serialization.ranges.*
import io.islandtime.measures.*
import io.islandtime.ranges.*
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.modules.serializersModuleOf
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class TestData(
    @ContextualSerialization val date: Date,
    @ContextualSerialization val time: Time,
    @ContextualSerialization val dateTime: DateTime,
    @ContextualSerialization val offsetTime: OffsetTime,
    @ContextualSerialization val offsetDateTime: OffsetDateTime,
    @ContextualSerialization val zonedDateTime: ZonedDateTime,
    @ContextualSerialization val yearMonth: YearMonth,
    @ContextualSerialization val instant: Instant,
    @ContextualSerialization val zoneRegion: TimeZone,
    @ContextualSerialization val fixedZone: TimeZone,
    @ContextualSerialization val duration: Duration,
    @ContextualSerialization val period: Period,
    @ContextualSerialization val dateRange: DateRange,
    @ContextualSerialization val dateTimeInterval: DateTimeInterval,
    @ContextualSerialization val instantInterval: InstantInterval,
    @ContextualSerialization val offsetDateTimeInterval: OffsetDateTimeInterval,
    @ContextualSerialization val zonedDateTimeInterval: ZonedDateTimeInterval
)

class SerializerTest {
    private val stringModule = serializersModuleOf(
        mapOf(
            Date::class to DateSerializer,
            Time::class to TimeSerializer,
            DateTime::class to DateTimeSerializer,
            OffsetTime::class to OffsetTimeSerializer,
            OffsetDateTime::class to OffsetDateTimeSerializer,
            ZonedDateTime::class to ZonedDateTimeSerializer,
            YearMonth::class to YearMonthSerializer,
            Instant::class to InstantSerializer,
            TimeZone::class to TimeZoneSerializer,
            TimeZone.Region::class to TimeZoneSerializer,
            TimeZone.FixedOffset::class to TimeZoneSerializer,
            Duration::class to DurationSerializer,
            Period::class to PeriodSerializer,
            DateRange::class to DateRangeSerializer,
            DateTimeInterval::class to DateTimeIntervalSerializer,
            InstantInterval::class to InstantIntervalSerializer,
            OffsetDateTimeInterval::class to OffsetDateTimeIntervalSerializer,
            ZonedDateTimeInterval::class to ZonedDateTimeIntervalSerializer
        )
    )

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
        instant = Instant(1L.seconds, 1.nanoseconds),
        zoneRegion = TimeZone("America/Non_Existent_Zone"),
        fixedZone = TimeZone.FixedOffset("+04:30"),
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
    fun `serialize and unserialize to json`() {
        val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true), stringModule)
        val text = json.stringify(TestData.serializer(), testData)

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
                    "instant": "1970-01-01T00:00:01.000000001Z",
                    "zoneRegion": "America/Non_Existent_Zone",
                    "fixedZone": "+04:30",
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

        val parsedTestData = json.parse(TestData.serializer(), text)
        assertEquals(testData, parsedTestData)
    }
}