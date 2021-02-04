package io.islandtime.extensions.serialization

import io.islandtime.UtcOffset
import io.islandtime.Year
import io.islandtime.toUtcOffset
import io.islandtime.toYear
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object YearSerializer : KSerializer<Year> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.YearSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Year) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Year {
        return decoder.decodeString().toYear()
    }
}

object UtcOffsetSerializer : KSerializer<UtcOffset> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("io.islandtime.UtcOffsetSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UtcOffset) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UtcOffset {
        return decoder.decodeString().toUtcOffset()
    }
}
