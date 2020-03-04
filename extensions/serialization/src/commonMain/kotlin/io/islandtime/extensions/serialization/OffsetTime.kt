package io.islandtime.extensions.serialization

import io.islandtime.OffsetTime
import io.islandtime.toOffsetTime
import kotlinx.serialization.*

@Serializer(forClass = OffsetTime::class)
object OffsetTimeSerializer : KSerializer<OffsetTime> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("OffsetTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetTime {
        return decoder.decodeString().toOffsetTime()
    }
}