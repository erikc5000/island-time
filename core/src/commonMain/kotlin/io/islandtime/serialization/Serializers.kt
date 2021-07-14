package io.islandtime.serialization

import io.islandtime.*
import io.islandtime.measures.Duration
import io.islandtime.measures.Period
import io.islandtime.measures.toDuration
import io.islandtime.measures.toPeriod
import io.islandtime.ranges.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object YearSerializer : KSerializer<Year> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.YearSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Year) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Year {
        return decoder.decodeString().toYear()
    }
}

object YearMonthSerializer : KSerializer<YearMonth> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.YearMonthSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: YearMonth) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): YearMonth {
        return decoder.decodeString().toYearMonth()
    }
}

object DateSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.DateSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Date {
        return decoder.decodeString().toDate()
    }
}

object TimeSerializer : KSerializer<Time> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.TimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Time) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Time {
        return decoder.decodeString().toTime()
    }
}

object DateTimeSerializer : KSerializer<DateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.DateTimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateTime {
        return decoder.decodeString().toDateTime()
    }
}

object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.OffsetDateTimeSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return decoder.decodeString().toOffsetDateTime()
    }
}

object OffsetTimeSerializer : KSerializer<OffsetTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.OffsetTimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetTime {
        return decoder.decodeString().toOffsetTime()
    }
}

object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.ZonedDateTimeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return decoder.decodeString().toZonedDateTime()
    }
}

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.InstantSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return decoder.decodeString().toInstant()
    }
}

object UtcOffsetSerializer : KSerializer<UtcOffset> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.UtcOffsetSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UtcOffset) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UtcOffset {
        return decoder.decodeString().toUtcOffset()
    }
}

object TimeZoneSerializer : KSerializer<TimeZone> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.TimeZoneSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TimeZone) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): TimeZone {
        return TimeZone(decoder.decodeString())
    }
}

object DateRangeSerializer : KSerializer<DateRange> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.DateRangeSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateRange) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateRange {
        return decoder.decodeString().toDateRange()
    }
}

object DateTimeIntervalSerializer : KSerializer<DateTimeInterval> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.DateTimeIntervalSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: DateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateTimeInterval {
        return decoder.decodeString().toDateTimeInterval()
    }
}

object InstantIntervalSerializer : KSerializer<InstantInterval> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.InstantIntervalSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: InstantInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): InstantInterval {
        return decoder.decodeString().toInstantInterval()
    }
}

object OffsetDateTimeIntervalSerializer : KSerializer<OffsetDateTimeInterval> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.OffsetDateTimeIntervalSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: OffsetDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTimeInterval {
        return decoder.decodeString().toOffsetDateTimeInterval()
    }
}

object ZonedDateTimeIntervalSerializer : KSerializer<ZonedDateTimeInterval> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.ZonedDateTimeIntervalSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: ZonedDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTimeInterval {
        return decoder.decodeString().toZonedDateTimeInterval()
    }
}

object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.DurationSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Duration {
        return decoder.decodeString().toDuration()
    }
}

object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.PeriodSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Period) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Period {
        return decoder.decodeString().toPeriod()
    }
}
