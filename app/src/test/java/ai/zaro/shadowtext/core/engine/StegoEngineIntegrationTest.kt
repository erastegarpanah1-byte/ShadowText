package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.ZeroWidthEncoder
import ai.zaro.shadowtext.core.format.PacketFormat
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StegoEngineIntegrationTest {

    private lateinit var encoder: StegoEncoder
    private lateinit var decoder: StegoDecoder

    @Before
    fun setUp() {
        val zwEncoder = ZeroWidthEncoder()
        encoder = StegoEncoder(
            encoder = zwEncoder,
            carrierTextProvider = GeneratedCarrierTextProvider(minSentences = 3),
        )
        decoder = StegoDecoder(listOf(zwEncoder))
    }

    @Test
    fun `full encode-decode round trip small text file`() {
        val payload = "The secret message to hide.".toByteArray(Charsets.UTF_8)
        val fileName = "secret.txt"
        val mimeType = "text/plain"

        val encodeResult = encoder.encode(payload, mimeType, fileName)
        assertNotNull(encodeResult.stegoText)
        assertTrue(encodeResult.stegoText.length > encodeResult.visibleText.length)
        assertTrue(encodeResult.invisibleCharCount > 0)

        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertArrayEquals(payload, decodeResult.payload)
        assertEquals(PacketFormat.PayloadType.PLAIN_TEXT, decodeResult.payloadType)
        assertEquals(fileName, decodeResult.metadata["filename"])
        assertEquals(mimeType, decodeResult.metadata["mimeType"])
    }

    @Test
    fun `full round trip binary payload`() {
        val payload = ByteArray(2048) { (it * 7 % 256).toByte() }
        val fileName = "data.bin"
        val mimeType = "application/octet-stream"

        val encodeResult = encoder.encode(payload, mimeType, fileName)
        val decodeResult = decoder.decode(encodeResult.stegoText)

        assertArrayEquals(payload, decodeResult.payload)
        assertEquals(fileName, decodeResult.metadata["filename"])
        assertEquals(payload.size, decodeResult.payload.size)
    }

    @Test
    fun `full round trip with no metadata`() {
        val payload = byteArrayOf(0xDE, 0xAD.toByte(), 0xBE, 0xEF.toByte())
        val encodeResult = encoder.encode(payload, null, null)
        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertArrayEquals(payload, decodeResult.payload)
    }

    @Test
    fun `detection finds hidden payload`() {
        val payload = "Hidden".toByteArray()
        val encodeResult = encoder.encode(payload, "text/plain", "test.txt")
        val detection = decoder.detect(encodeResult.stegoText)
        assertTrue(detection.hasHiddenPayload)
        assertEquals("zwc", detection.encodingScheme)
    }

    @Test
    fun `detection returns false for plain text`() {
        val detection = decoder.detect("This is just ordinary text without any hidden data.")
        assertFalse(detection.hasHiddenPayload)
    }

    @Test
    fun `detection returns false for empty text`() {
        val detection = decoder.detect("")
        assertFalse(detection.hasHiddenPayload)
    }

    @Test(expected = StegoException::class)
    fun `decode throws on plain text`() {
        decoder.decode("No hidden data in this text at all.")
    }

    @Test
    fun `encode result contains expected metadata`() {
        val payload = byteArrayOf(1, 2, 3, 4, 5)
        val encodeResult = encoder.encode(payload, "image/png", "photo.png")
        assertEquals(5, encodeResult.payloadSizeBytes)
        assertEquals("zwc", encodeResult.encodingScheme)
        assertTrue(encodeResult.invisibleCharCount > 0)
        assertTrue(encodeResult.visibleText.isNotEmpty())
    }

    @Test
    fun `round trip with PNG payload type`() {
        val payload = byteArrayOf(
            0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
        )
        val encodeResult = encoder.encode(payload, "image/png", "test.png")
        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertEquals(PacketFormat.PayloadType.IMAGE_PNG, decodeResult.payloadType)
        assertEquals("PNG Image", decodeResult.payloadTypeLabel)
        assertArrayEquals(payload, decodeResult.payload)
    }

    @Test
    fun `round trip with APK payload type`() {
        val payload = byteArrayOf(0x50, 0x4B, 0x03, 0x04)
        val encodeResult = encoder.encode(payload, "application/vnd.android.package-archive", "app.apk")
        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertEquals(PacketFormat.PayloadType.ANDROID_APK, decodeResult.payloadType)
        assertEquals("Android APK", decodeResult.payloadTypeLabel)
    }
}
