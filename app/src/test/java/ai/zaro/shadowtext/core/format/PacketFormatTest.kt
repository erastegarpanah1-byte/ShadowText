package ai.zaro.shadowtext.core.format

import org.junit.Assert.*
import org.junit.Test

class PacketFormatTest {

    @Test fun `round trip simple packet`() {
        val original = Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE, PacketFormat.PayloadType.PLAIN_TEXT,
            "Hello".toByteArray(Charsets.UTF_8), mapOf("filename" to "test.txt"))
        val deserialized = PacketDeserializer.deserialize(PacketSerializer.serialize(original))
        assertArrayEquals(original.payload, deserialized.payload)
        assertEquals("test.txt", deserialized.metadata["filename"])
    }

    @Test fun `serialized size matches expected`() {
        val packet = Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE, PacketFormat.PayloadType.RAW_BYTES, ByteArray(100))
        assertEquals(128, PacketSerializer.serialize(packet).size)
    }

    @Test(expected = PacketFormatException::class)
    fun `deserialize rejects bad magic`() {
        PacketDeserializer.deserialize(ByteArray(28) { 0x00.toByte() })
    }

    @Test(expected = PacketFormatException::class)
    fun `deserialize rejects truncated data`() {
        PacketDeserializer.deserialize(ByteArray(10))
    }

    @Test fun `payload type from mime type`() {
        assertEquals(PacketFormat.PayloadType.IMAGE_PNG, PacketFormat.PayloadType.fromMimeType("image/png"))
        assertEquals(PacketFormat.PayloadType.PLAIN_TEXT, PacketFormat.PayloadType.fromMimeType("text/plain"))
        assertEquals(PacketFormat.PayloadType.UNKNOWN, PacketFormat.PayloadType.fromMimeType(null))
    }
}
