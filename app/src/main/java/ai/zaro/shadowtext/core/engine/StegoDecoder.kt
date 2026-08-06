package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.EncodingException
import ai.zaro.shadowtext.core.encoding.InvisibleEncoder
import ai.zaro.shadowtext.core.format.PacketDeserializer
import ai.zaro.shadowtext.core.format.PacketFormat
import ai.zaro.shadowtext.core.format.PacketFormatException

class StegoDecoder(
    private val encoders: List<InvisibleEncoder>,
) {

    fun decode(text: String): DecodeResult {
        val errors = mutableListOf<String>()
        for (encoder in encoders) {
            if (!encoder.containsEncodedData(text)) {
                errors.add("${encoder.identifier}: no encoded data detected")
                continue
            }
            try {
                val invisible = encoder.extractInvisible(text)
                val binaryBlob = encoder.decode(invisible)
                val packet = PacketDeserializer.deserialize(binaryBlob)
                return DecodeResult(
                    payload = packet.payload,
                    payloadType = packet.payloadType,
                    payloadTypeLabel = PacketFormat.PayloadType.toLabel(packet.payloadType),
                    metadata = packet.metadata,
                    encodingScheme = encoder.identifier,
                )
            } catch (e: EncodingException) {
                errors.add("${encoder.identifier}: encoding error: ${e.message}")
            } catch (e: PacketFormatException) {
                errors.add("${encoder.identifier}: format error: ${e.message}")
            }
        }
        throw StegoException(
            "No hidden data could be decoded. Errors:\n${errors.joinToString("\n")}"
        )
    }

    fun detect(text: String): DetectionResult {
        for (encoder in encoders) {
            if (!encoder.containsEncodedData(text)) continue
            try {
                val invisible = encoder.extractInvisible(text)
                val binaryBlob = encoder.decode(invisible)
                val packet = PacketDeserializer.deserialize(binaryBlob)
                return DetectionResult(
                    hasHiddenPayload = true,
                    encodingScheme = encoder.identifier,
                    payloadTypeLabel = PacketFormat.PayloadType.toLabel(packet.payloadType),
                    payloadSizeBytes = packet.payload.size,
                )
            } catch (_: Exception) {
                return DetectionResult(
                    hasHiddenPayload = true,
                    encodingScheme = encoder.identifier,
                )
            }
        }
        return DetectionResult(hasHiddenPayload = false)
    }
}

class StegoException(message: String, cause: Throwable? = null) :
    Exception(message, cause)
