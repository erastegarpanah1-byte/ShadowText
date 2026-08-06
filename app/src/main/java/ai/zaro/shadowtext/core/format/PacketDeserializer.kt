package ai.zaro.shadowtext.core.format

import java.util.zip.CRC32
import kotlinx.serialization.json.Json

internal object PacketDeserializer {

    private val json = Json { ignoreUnknownKeys = true }

    fun deserialize(data: ByteArray): Packet {
        require(data.size >= PacketFormat.TOTAL_MIN_SIZE) {
            "Data too short: ${data.size} bytes, minimum is ${PacketFormat.TOTAL_MIN_SIZE}"
        }

        var offset = 0

        val magic = readIntLe(data, offset)
        offset += PacketFormat.MAGIC_SIZE
        if (magic != PacketFormat.MAGIC) {
            throw PacketFormatException(
                "Invalid magic number: 0x%08X, expected 0x%08X".format(magic, PacketFormat.MAGIC)
            )
        }

        val version = readShortLe(data, offset)
        offset += PacketFormat.VERSION_SIZE

        val flags = data[offset]
        offset += PacketFormat.FLAGS_SIZE

        val payloadType = data[offset]
        offset += PacketFormat.PAYLOAD_TYPE_SIZE

        val payloadSize = readLongLe(data, offset)
        offset += PacketFormat.PAYLOAD_SIZE_FIELD

        val metadataLen = readIntLe(data, offset)
        offset += PacketFormat.METADATA_LEN_SIZE

        offset += PacketFormat.RESERVED_SIZE

        val checksumOffset = offset + metadataLen + payloadSize.toInt()
        if (checksumOffset + PacketFormat.CHECKSUM_SIZE > data.size) {
            throw PacketFormatException(
                "Truncated data: header indicates ${checksumOffset + PacketFormat.CHECKSUM_SIZE} " +
                        "bytes but only ${data.size} available"
            )
        }

        val crc = CRC32()
        crc.update(data, 0, checksumOffset)
        val expectedCrc = crc.value.toInt()
        val actualCrc = readIntLe(data, checksumOffset)
        if (expectedCrc != actualCrc) {
            throw PacketFormatException(
                "Checksum mismatch: computed 0x%08X, stored 0x%08X".format(expectedCrc, actualCrc)
            )
        }

        val metadata: Map<String, String> = if (metadataLen > 0) {
            try {
                val metaBytes = data.copyOfRange(offset, offset + metadataLen)
                json.decodeFromString<Map<String, String>>(String(metaBytes, Charsets.UTF_8))
            } catch (e: Exception) {
                emptyMap()
            }
        } else {
            emptyMap()
        }
        offset += metadataLen

        val payload = data.copyOfRange(offset, offset + payloadSize.toInt())

        return Packet(
            version = version,
            flags = flags,
            payloadType = payloadType,
            payload = payload,
            metadata = metadata,
        )
    }

    private fun readIntLe(data: ByteArray, offset: Int): Int {
        return (data[offset].toInt() and 0xFF) or
                ((data[offset + 1].toInt() and 0xFF) shl 8) or
                ((data[offset + 2].toInt() and 0xFF) shl 16) or
                ((data[offset + 3].toInt() and 0xFF) shl 24)
    }

    private fun readShortLe(data: ByteArray, offset: Int): Short {
        return ((data[offset].toInt() and 0xFF) or
                ((data[offset + 1].toInt() and 0xFF) shl 8)).toShort()
    }

    private fun readLongLe(data: ByteArray, offset: Int): Long {
        var result = 0L
        for (i in 0 until 8) {
            result = result or ((data[offset + i].toLong() and 0xFF) shl (i * 8))
        }
        return result
    }
}

class PacketFormatException(message: String) : Exception(message)
