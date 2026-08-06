package ai.zaro.shadowtext.core.engine

data class EncodeResult(
    val stegoText: String,
    val visibleText: String,
    val payloadSizeBytes: Int,
    val invisibleCharCount: Int,
    val encodingScheme: String,
)

data class DecodeResult(
    val payload: ByteArray,
    val payloadType: Byte,
    val payloadTypeLabel: String,
    val metadata: Map<String, String>,
    val encodingScheme: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DecodeResult
        return payloadType == other.payloadType &&
                payloadTypeLabel == other.payloadTypeLabel &&
                metadata == other.metadata &&
                encodingScheme == other.encodingScheme &&
                payload.contentEquals(other.payload)
    }
    override fun hashCode(): Int {
        var result = payload.contentHashCode()
        result = 31 * result + payloadType.hashCode()
        result = 31 * result + payloadTypeLabel.hashCode()
        result = 31 * result + metadata.hashCode()
        result = 31 * result + encodingScheme.hashCode()
        return result
    }
}

data class DetectionResult(
    val hasHiddenPayload: Boolean,
    val encodingScheme: String? = null,
    val payloadTypeLabel: String? = null,
    val payloadSizeBytes: Int = 0,
)
