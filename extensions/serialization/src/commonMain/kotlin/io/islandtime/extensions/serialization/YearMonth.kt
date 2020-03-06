package io.islandtime.extensions.serialization

import io.islandtime.YearMonth
import io.islandtime.toYearMonth
import kotlinx.serialization.*

object YearMonthSerializer : KSerializer<YearMonth> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.YearMonthSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: YearMonth) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): YearMonth {
        return decoder.decodeString().toYearMonth()
    }
}