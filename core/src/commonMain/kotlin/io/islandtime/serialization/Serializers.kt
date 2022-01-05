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

object YearIsoSerializer : KSerializer<Year> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.YearIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Year) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Year {
        return decoder.decodeString().toYear()
    }
}

object YearMonthIsoSerializer : KSerializer<YearMonth> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.YearMonthIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: YearMonth) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): YearMonth {
        return decoder.decodeString().toYearMonth()
    }
}

object DateIsoSerializer : KSerializer<Date> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.DateIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Date {
        return decoder.decodeString().toDate()
    }
}

object TimeIsoSerializer : KSerializer<Time> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.TimeIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Time) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Time {
        return decoder.decodeString().toTime()
    }
}

object DateTimeIsoSerializer : KSerializer<DateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.DateTimeIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateTime {
        return decoder.decodeString().toDateTime()
    }
}

object OffsetDateTimeIsoSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.OffsetDateTimeIsoSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return decoder.decodeString().toOffsetDateTime()
    }
}

object OffsetTimeIsoSerializer : KSerializer<OffsetTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.OffsetTimeIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetTime {
        return decoder.decodeString().toOffsetTime()
    }
}

object ZonedDateTimeIsoSerializer : KSerializer<ZonedDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.ZonedDateTimeIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        return decoder.decodeString().toZonedDateTime()
    }
}

object InstantIsoSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.InstantIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return decoder.decodeString().toInstant()
    }
}

object UtcOffsetIsoSerializer : KSerializer<UtcOffset> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.UtcOffsetIsoSerializer", PrimitiveKind.STRING)

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

object DateRangeIsoSerializer : KSerializer<DateRange> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.DateRangeIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: DateRange) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateRange {
        return decoder.decodeString().toDateRange()
    }
}

object DateTimeIntervalIsoSerializer : KSerializer<DateTimeInterval> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.DateTimeIntervalIsoSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: DateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): DateTimeInterval {
        return decoder.decodeString().toDateTimeInterval()
    }
}

object InstantIntervalIsoSerializer : KSerializer<InstantInterval> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.InstantIntervalIsoSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: InstantInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): InstantInterval {
        return decoder.decodeString().toInstantInterval()
    }
}

object OffsetDateTimeIntervalIsoSerializer : KSerializer<OffsetDateTimeInterval> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.OffsetDateTimeIntervalIsoSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: OffsetDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTimeInterval {
        return decoder.decodeString().toOffsetDateTimeInterval()
    }
}

object ZonedDateTimeIntervalIsoSerializer : KSerializer<ZonedDateTimeInterval> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "io.islandtime.serialization.ZonedDateTimeIntervalIsoSerializer",
        PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: ZonedDateTimeInterval) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): ZonedDateTimeInterval {
        return decoder.decodeString().toZonedDateTimeInterval()
    }
}

object DurationIsoSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.DurationIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Duration {
        return decoder.decodeString().toDuration()
    }
}

object PeriodIsoSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.serialization.PeriodIsoSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Period) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Period {
        return decoder.decodeString().toPeriod()
    }
}
