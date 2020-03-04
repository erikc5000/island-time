package io.islandtime.extensions.serialization.measures

import io.islandtime.measures.*
import kotlinx.serialization.*

@Serializer(forClass = Period::class)
object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("Period", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Period) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Period {
        return decoder.decodeString().toPeriod()
    }
}