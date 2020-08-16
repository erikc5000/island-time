package io.islandtime.extensions.serialization.measures

import io.islandtime.measures.Period
import io.islandtime.measures.toPeriod
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.measures.PeriodSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Period) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Period {
        return decoder.decodeString().toPeriod()
    }
}
