package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.InvisibleEncoder
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
        val stego = embedAtBreak(carrierText, inv)
        return EncodeResult(stego, carrierText, payload.size, encoder.extractInvisible(inv).length, encoder.identifier)
    }
    private fun embedAtBreak(text: String, p: String): String {
        if (text.isEmpty()) return p
        val i = text.indexOf(' ')
        return if (i >= 0) text.substring(0, i) + p + text.substring(i) else text + p
    }
}
