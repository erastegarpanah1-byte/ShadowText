package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.InvisibleEncoder
import ai.zaro.shadowtext.core.format.Packet
import ai.zaro.shadowtext.core.format.PacketFormat
import ai.zaro.shadowtext.core.format.PacketSerializer

class StegoEncoder(private val encoder: InvisibleEncoder) {
    fun encode(payload: ByteArray, mimeType: String? = null, fileName: String? = null, carrierText: String): EncodeResult {
        val pt = PacketFormat.PayloadType.fromMimeType(mimeType)
        val meta = buildMap {
            fileName?.let { put("filename", it) }
            mimeType?.let { put("mimeType", it) }
            put("encodedAt", System.currentTimeMillis().toString())
            put("encodingScheme", encoder.identifier)
        }
        val pkt = Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE, pt, payload, meta)
        val blob = PacketSerializer.serialize(pkt)
        val inv = encoder.encode(blob)
        val stego = carrierText + inv
        return EncodeResult(stego, carrierText, payload.size, encoder.extractInvisible(inv).length, encoder.identifier)
    }
}
