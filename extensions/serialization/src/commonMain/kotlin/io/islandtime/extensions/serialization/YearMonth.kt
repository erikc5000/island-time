package io.islandtime.extensions.serialization

import io.islandtime.YearMonth
import io.islandtime.toYearMonth
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object YearMonthSerializer : KSerializer<YearMonth> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.YearMonthSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: YearMonth) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): YearMonth {
        return decoder.decodeString().toYearMonth()
    }
}
