package io.islandtime.extensions.serialization.measures

import io.islandtime.measures.*
import kotlinx.serialization.*

object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("io.islandtime.measures.PeriodSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Period) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Period {
        return decoder.decodeString().toPeriod()
    }
}