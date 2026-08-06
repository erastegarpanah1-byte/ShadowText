package ai.zaro.shadowtext.core.format

import java.io.ByteArrayOutputStream
import java.util.zip.CRC32
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

internal object PacketSerializer {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun serialize(packet: Packet): ByteArray {
        val metadataJson = json.encodeToString(packet.metadata).toByteArray(Charsets.UTF_8)

        val out = ByteArrayOutputStream()
        out.write(intToLeBytes(PacketFormat.MAGIC, PacketFormat.MAGIC_SIZE))
        out.write(shortToLeBytes(packet.version))
        out.write(packet.flags.toInt())
        out.write(packet.payloadType.toInt())
        out.write(longToLeBytes(packet.payload.size.toLong()))
        out.write(intToLeBytes(metadataJson.size, PacketFormat.METADATA_LEN_SIZE))
        out.write(intToLeBytes(0, PacketFormat.RESERVED_SIZE))
        out.write(metadataJson)
        out.write(packet.payload)

        val body = out.toByteArray()
        val crc = CRC32()
        crc.update(body)
        val checksumBytes = intToLeBytes(crc.value.toInt(), PacketFormat.CHECKSUM_SIZE)

        return body + checksumBytes
    }

    private fun intToLeBytes(value: Int, size: Int): ByteArray {
        val arr = ByteArray(size)
        for (i in 0 until size) arr[i] = (value shr (i * 8)).toByte()
        return arr
    }

    private fun shortToLeBytes(value: Short): ByteArray {
        return byteArrayOf(value.toByte(), (value.toInt() shr 8).toByte())
    }

    private fun longToLeBytes(value: Long): ByteArray {
        val arr = ByteArray(8)
        for (i in 0 until 8) arr[i] = (value shr (i * 8)).toByte()
        return arr
    }
}
