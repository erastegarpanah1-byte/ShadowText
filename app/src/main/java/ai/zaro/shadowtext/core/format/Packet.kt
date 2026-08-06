package ai.zaro.shadowtext.core.format

/**
 * Represents the decoded contents of a ShadowText packet.
 */
data class Packet(
    val version: Short,
    val flags: Byte,
    val payloadType: Byte,
    val payload: ByteArray,
    val metadata: Map<String, String> = emptyMap(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Packet
        return version == other.version &&
                flags == other.flags &&
                payloadType == other.payloadType &&
                payload.contentEquals(other.payload) &&
                metadata == other.metadata
    }

    override fun hashCode(): Int {
        var result = version.hashCode()
        result = 31 * result + flags
        result = 31 * result + payloadType
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + metadata.hashCode()
        return result
    }

    override fun toString(): String {
        return "Packet(version=$version, flags=$flags, " +
                "payloadType=$payloadType (${PacketFormat.PayloadType.toLabel(payloadType)}), " +
                "payloadSize=${payload.size}, metadata=$metadata)"
    }
}
