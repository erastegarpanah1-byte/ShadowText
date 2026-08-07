package ai.zaro.shadowtext.core.format

import org.junit.Assert.*
import org.junit.Test

class PacketFormatTest {
    @Test fun `round trip`() {
        val o = Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE,
            PacketFormat.PayloadType.PLAIN_TEXT, "Hello".toByteArray(Charsets.UTF_8),
            mapOf("f" to "t.txt"))
        val d = PacketDeserializer.deserialize(PacketSerializer.serialize(o))
        assertArrayEquals(o.payload, d.payload)
    }
    @Test fun `serialized has valid size`() {
        val s = PacketSerializer.serialize(Packet(PacketFormat.CURRENT_VERSION, PacketFormat.Flags.NONE,
            PacketFormat.PayloadType.RAW_BYTES, ByteArray(100)))
        assertTrue("size ${s.size} >= 128", s.size >= 128)
    }
    @Test(expected = PacketFormatException::class)
    fun `rejects bad magic`() { PacketDeserializer.deserialize(ByteArray(30) { 0x00.toByte() }) }
    @Test(expected = PacketFormatException::class)
    fun `rejects truncated`() { PacketDeserializer.deserialize(ByteArray(10)) }
    @Test fun `mime mapping`() {
        assertEquals(PacketFormat.PayloadType.IMAGE_PNG, PacketFormat.PayloadType.fromMimeType("image/png"))
        assertEquals(PacketFormat.PayloadType.UNKNOWN, PacketFormat.PayloadType.fromMimeType(null))
    }
}
