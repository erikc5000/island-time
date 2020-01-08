package io.islandtime.extensions.serialization.measures

import io.islandtime.measures.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Period::class)
object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("Period")

    override fun serialize(encoder: Encoder, obj: Period) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): Period {
        return decoder.decodeString().toPeriod()
    }
}