package ai.zaro.shadowtext.core.format

import org.junit.Assert.*
import org.junit.Test

class PacketFormatTest {

    @Test
    fun `round trip simple packet`() {
        val original = Packet(
            version = PacketFormat.CURRENT_VERSION, flags = PacketFormat.Flags.NONE,
            payloadType = PacketFormat.PayloadType.PLAIN_TEXT,
            payload = "Hello, World!".toByteArray(Charsets.UTF_8),
            metadata = mapOf("filename" to "test.txt"),
        )
        val serialized = PacketSerializer.serialize(original)
        val deserialized = PacketDeserializer.deserialize(serialized)
        assertArrayEquals(original.payload, deserialized.payload)
    }

    @Test
    fun `round trip empty payload`() {
        val original = Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE, PacketFormat.PayloadType.RAW_BYTES, ByteArray(0))
        val deserialized = PacketDeserializer.deserialize(PacketSerializer.serialize(original))
        assertEquals(0, deserialized.payload.size)
    }

    @Test
    fun `serialized size matches expected`() {
        val serialized = PacketSerializer.serialize(
            Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE, PacketFormat.PayloadType.RAW_BYTES, ByteArray(100))
        )
        assertEquals(128, serialized.size)
    }

    @Test(expected = PacketFormatException::class)
    fun `deserialize rejects bad magic number`() {
        PacketDeserializer.deserialize(ByteArray(28) { 0x00.toByte() })
    }

    @Test(expected = PacketFormatException::class)
    fun `deserialize rejects truncated data`() {
        PacketDeserializer.deserialize(byteArrayOf(0x58.toByte(), 0x44.toByte(), 0x54.toByte(), 0x53.toByte()))
    }

    @Test(expected = PacketFormatException::class)
    fun `deserialize rejects corrupted checksum`() {
        val serialized = PacketSerializer.serialize(
            Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE, PacketFormat.PayloadType.RAW_BYTES, byteArrayOf(1.toByte(), 2.toByte(), 3.toByte()))
        )
        serialized[serialized.size - 1] = (serialized[serialized.size - 1] + 1).toByte()
        PacketDeserializer.deserialize(serialized)
    }

    @Test
    fun `payload type mapping from mime type`() {
        assertEquals(PacketFormat.PayloadType.IMAGE_PNG, PacketFormat.PayloadType.fromMimeType("image/png"))
        assertEquals(PacketFormat.PayloadType.VIDEO_MP4, PacketFormat.PayloadType.fromMimeType("video/mp4"))
        assertEquals(PacketFormat.PayloadType.UNKNOWN, PacketFormat.PayloadType.fromMimeType(null))
    }
}
