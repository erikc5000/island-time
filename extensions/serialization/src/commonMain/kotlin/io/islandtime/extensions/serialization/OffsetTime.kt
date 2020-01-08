package io.islandtime.extensions.serialization

import io.islandtime.OffsetTime
import io.islandtime.toOffsetTime
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = OffsetTime::class)
object OffsetTimeSerializer : KSerializer<OffsetTime> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("OffsetTime")

    override fun serialize(encoder: Encoder, obj: OffsetTime) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetTime {
        return decoder.decodeString().toOffsetTime()
    }
}