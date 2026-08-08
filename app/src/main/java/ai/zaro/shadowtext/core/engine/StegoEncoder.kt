package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.InvisibleEncoder
import ai.zaro.shadowtext.core.format.Packet
import ai.zaro.shadowtext.core.format.PacketFormat
import ai.zaro.shadowtext.core.format.PacketSerializer

class StegoEncoder(
    private val encoder: InvisibleEncoder,
) {
    fun encode(payload: ByteArray, mimeType: String? = null, fileName: String? = null, carrierText: String): EncodeResult {
        val payloadType = PacketFormat.PayloadType.fromMimeType(mimeType)
        val metadata = buildMap {
            fileName?.let { put("filename", it) }
            mimeType?.let { put("mimeType", it) }
            put("encodedAt", System.currentTimeMillis().toString())
            put("encodingScheme", encoder.identifier)
        }
        val packet = Packet(version = PacketFormat.CURRENT_VERSION, flags = PacketFormat.Flags.NONE, payloadType = payloadType, payload = payload, metadata = metadata)
        val binaryBlob = PacketSerializer.serialize(packet)
        val invisiblePayload = encoder.encode(binaryBlob)
        val stegoText = embedPayload(carrierText, invisiblePayload)
        return EncodeResult(stegoText = stegoText, visibleText = carrierText, payloadSizeBytes = payload.size, invisibleCharCount = encoder.extractInvisible(invisiblePayload).length, encodingScheme = encoder.identifier)
    }
    private fun embedPayload(carrierText: String, invisiblePayload: String): String {
        if (carrierText.isEmpty()) return invisiblePayload
        val ip = findInsertionPoint(carrierText)
        return carrierText.substring(0, ip) + invisiblePayload + carrierText.substring(ip)
    }
    private fun findInsertionPoint(text: String): Int {
        for (i in text.indices) { val ch = text[i]; if (ch == '.' || ch == '!' || ch == '?' || ch == '\n') { var j = i + 1; while (j < text.length && text[j].isWhitespace()) j++; return j } }
        return text.length / 2
    }
}
