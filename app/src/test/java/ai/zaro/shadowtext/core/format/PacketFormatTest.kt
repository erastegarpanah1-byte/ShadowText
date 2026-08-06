package ai.zaro.shadowtext.core.format

import org.junit.Assert.*
import org.junit.Test

class PacketFormatTest {

    @Test
    fun `round trip simple packet`() {
        val original = Packet(
            version = PacketFormat.CURRENT_VERSION,
            flags = PacketFormat.Flags.NONE,
            payloadType = PacketFormat.PayloadType.PLAIN_TEXT,
            payload = "Hello, World!".toByteArray(Charsets.UTF_8),
            metadata = mapOf("filename" to "test.txt", "mimeType" to "text/plain"),
        )
        val serialized = PacketSerializer.serialize(original)
        val deserialized = PacketDeserializer.deserialize(serialized)
        assertEquals(original.version, deserialized.version)
        assertEquals(original.flags, deserialized.flags)
        assertEquals(original.payloadType, deserialized.payloadType)
        assertArrayEquals(original.payload, deserialized.payload)
        assertEquals(original.metadata, deserialized.metadata)
    }

    @Test
    fun `round trip empty payload`() {
        val original = Packet(
            version = PacketFormat.CURRENT_VERSION,
            flags = PacketFormat.Flags.NONE,
            payloadType = PacketFormat.PayloadType.RAW_BYTES,
            payload = ByteArray(0),
            metadata = emptyMap(),
        )
        val serialized = PacketSerializer.serialize(original)
        val deserialized = PacketDeserializer.deserialize(serialized)
        assertEquals(0, deserialized.payload.size)
    }

    @Test
    fun `round trip binary payload`() {
        val original = Packet(
            version = PacketFormat.CURRENT_VERSION,
            flags = PacketFormat.Flags.NONE,
            payloadType = PacketFormat.PayloadType.IMAGE_PNG,
            payload = ByteArray(1024) { (it % 256).toByte() },
            metadata = mapOf("filename" to "image.png"),
        )
        val serialized = PacketSerializer.serialize(original)
        val deserialized = PacketDeserializer.deserialize(serialized)
        assertEquals(PacketFormat.PayloadType.IMAGE_PNG, deserialized.payloadType)
        assertArrayEquals(original.payload, deserialized.payload)
        assertEquals("image.png", deserialized.metadata["filename"])
    }

    @Test
    fun `serialized size matches expected`() {
        val payload = ByteArray(100)
        val original = Packet(
            version = PacketFormat.CURRENT_VERSION,
            flags = PacketFormat.Flags.NONE,
            payloadType = PacketFormat.PayloadType.RAW_BYTES,
            payload = payload,
            metadata = emptyMap(),
        )
        val serialized = PacketSerializer.serialize(original)
        assertEquals(128, serialized.size)
    }

    @Test(expected = PacketFormatException::class)
    fun `deserialize rejects bad magic number`() {
        val data = ByteArray(28) { 0x00 }
        PacketDeserializer.deserialize(data)
    }

    @Test(expected = PacketFormatException::class)
    fun `deserialize rejects truncated data`() {
        val data = byteArrayOf(0x58, 0x44, 0x54, 0x53)
        PacketDeserializer.deserialize(data)
    }

    @Test(expected = PacketFormatException::class)
    fun `deserialize rejects corrupted checksum`() {
        val original = Packet(
            version = PacketFormat.CURRENT_VERSION,
            flags = PacketFormat.Flags.NONE,
            payloadType = PacketFormat.PayloadType.RAW_BYTES,
            payload = byteArrayOf(1, 2, 3),
            metadata = emptyMap(),
        )
        val serialized = PacketSerializer.serialize(original)
        serialized[serialized.size - 1] = (serialized[serialized.size - 1] + 1).toByte()
        PacketDeserializer.deserialize(serialized)
    }

    @Test
    fun `deserialize handles metadata correctly`() {
        val metadata = mapOf(
            "filename" to "document.pdf",
            "mimeType" to "application/pdf",
            "author" to "Test User",
        )
        val original = Packet(
            version = PacketFormat.CURRENT_VERSION,
            flags = PacketFormat.Flags.NONE,
            payloadType = PacketFormat.PayloadType.DOCUMENT_PDF,
            payload = byteArrayOf(0x25, 0x50, 0x44, 0x46),
            metadata = metadata,
        )
        val serialized = PacketSerializer.serialize(original)
        val deserialized = PacketDeserializer.deserialize(serialized)
        assertEquals("document.pdf", deserialized.metadata["filename"])
        assertEquals("application/pdf", deserialized.metadata["mimeType"])
        assertEquals("Test User", deserialized.metadata["author"])
    }

    @Test
    fun `payload type mapping from mime type`() {
        assertEquals(PacketFormat.PayloadType.IMAGE_PNG, PacketFormat.PayloadType.fromMimeType("image/png"))
        assertEquals(PacketFormat.PayloadType.VIDEO_MP4, PacketFormat.PayloadType.fromMimeType("video/mp4"))
        assertEquals(PacketFormat.PayloadType.DOCUMENT_PDF, PacketFormat.PayloadType.fromMimeType("application/pdf"))
        assertEquals(PacketFormat.PayloadType.ANDROID_APK, PacketFormat.PayloadType.fromMimeType("application/vnd.android.package-archive"))
        assertEquals(PacketFormat.PayloadType.PLAIN_TEXT, PacketFormat.PayloadType.fromMimeType("text/plain"))
        assertEquals(PacketFormat.PayloadType.UNKNOWN, PacketFormat.PayloadType.fromMimeType("application/octet-stream"))
        assertEquals(PacketFormat.PayloadType.UNKNOWN, PacketFormat.PayloadType.fromMimeType(null))
    }

    @Test
    fun `payload type labels are non-empty`() {
        val types = listOf(
            PacketFormat.PayloadType.RAW_BYTES,
            PacketFormat.PayloadType.PLAIN_TEXT,
            PacketFormat.PayloadType.IMAGE_PNG,
            PacketFormat.PayloadType.VIDEO_MP4,
            PacketFormat.PayloadType.AUDIO_MP3,
            PacketFormat.PayloadType.DOCUMENT_PDF,
            PacketFormat.PayloadType.ARCHIVE_ZIP,
            PacketFormat.PayloadType.ANDROID_APK,
            PacketFormat.PayloadType.UNKNOWN,
        )
        types.forEach { type ->
            val label = PacketFormat.PayloadType.toLabel(type)
            assertTrue("Label for 0x%02X should not be empty".format(type), label.isNotEmpty())
        }
    }
}
