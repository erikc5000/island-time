package io.islandtime.extensions.serialization.measures

import io.islandtime.measures.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = Duration::class)
object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor = StringDescriptor.withName("Duration")

    override fun serialize(encoder: Encoder, obj: Duration) {
        encoder.encodeString(obj.toString())
    }

    override fun deserialize(decoder: Decoder): Duration {
        return decoder.decodeString().toDuration()
    }
}