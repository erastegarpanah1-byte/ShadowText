package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.InvisibleEncoder
import ai.zaro.shadowtext.core.encoding.SpaceHomoglyphEncoder
import ai.zaro.shadowtext.core.encoding.VariationSelectorEncoder
import ai.zaro.shadowtext.core.format.Packet
import ai.zaro.shadowtext.core.format.PacketFormat
import ai.zaro.shadowtext.core.format.PacketSerializer

class StegoEncoder(private val encoder: InvisibleEncoder) {

    fun encode(payload: ByteArray, mimeType: String?, fileName: String?, carrierText: String): EncodeResult {
        val pt = PacketFormat.PayloadType.fromMimeType(mimeType)
        val meta = mapOf(
            "filename" to (fileName ?: ""),
            "mimeType" to (mimeType ?: ""),
            "encodedAt" to System.currentTimeMillis().toString(),
            "encodingScheme" to encoder.identifier
        ).filterValues { it.isNotEmpty() }
        val pkt = Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE, pt, payload, meta)
        val inv = encoder.encode(PacketSerializer.serialize(pkt))

        val stego = when (encoder) {
            is SpaceHomoglyphEncoder -> encoder.embed(carrierText, inv)
            is VariationSelectorEncoder -> encoder.embed(carrierText, inv)
            else -> {
                if (carrierText.isEmpty()) inv
                else {
                    val i = carrierText.indexOf(' ')
                    if (i >= 0) carrierText.substring(0, i) + inv + carrierText.substring(i)
                    else carrierText + inv
                }
            }
        }
        return EncodeResult(stego, carrierText, payload.size, encoder.extractInvisible(inv).length, encoder.identifier)
    }
}
