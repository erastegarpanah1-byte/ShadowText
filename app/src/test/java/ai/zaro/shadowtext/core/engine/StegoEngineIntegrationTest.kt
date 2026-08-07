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
        encoder = StegoEncoder(encoder = zwEncoder, carrierTextProvider = GeneratedCarrierTextProvider(3))
        decoder = StegoDecoder(listOf(zwEncoder))
    }

    @Test
    fun `full encode-decode round trip small text file`() {
        val payload = "The secret message to hide.".toByteArray(Charsets.UTF_8)
        val encodeResult = encoder.encode(payload, "text/plain", "secret.txt")
        assertNotNull(encodeResult.stegoText)
        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertArrayEquals(payload, decodeResult.payload)
    }

    @Test
    fun `full round trip binary payload`() {
        val payload = ByteArray(2048) { (it * 7 % 256).toByte() }
        val encodeResult = encoder.encode(payload, "application/octet-stream", "data.bin")
        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertArrayEquals(payload, decodeResult.payload)
    }

    @Test
    fun `full round trip with no metadata`() {
        val payload = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val encodeResult = encoder.encode(payload, null, null)
        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertArrayEquals(payload, decodeResult.payload)
    }

    @Test
    fun `detection finds hidden payload`() {
        val encodeResult = encoder.encode("Hidden".toByteArray(), "text/plain", "test.txt")
        val detection = decoder.detect(encodeResult.stegoText)
        assertTrue(detection.hasHiddenPayload)
    }

    @Test
    fun `detection returns false for plain text`() {
        assertFalse(decoder.detect("Just ordinary text.").hasHiddenPayload)
    }

    @Test
    fun `detection returns false for empty text`() {
        assertFalse(decoder.detect("").hasHiddenPayload)
    }

    @Test(expected = StegoException::class)
    fun `decode throws on plain text`() {
        decoder.decode("No hidden data.")
    }

    @Test
    fun `encode result contains expected metadata`() {
        val payload = byteArrayOf(1.toByte(), 2.toByte(), 3.toByte(), 4.toByte(), 5.toByte())
        val encodeResult = encoder.encode(payload, "image/png", "photo.png")
        assertEquals(5, encodeResult.payloadSizeBytes)
        assertEquals("zwc", encodeResult.encodingScheme)
    }

    @Test
    fun `round trip with PNG payload type`() {
        val payload = byteArrayOf(
            0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
            0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte(),
        )
        val encodeResult = encoder.encode(payload, "image/png", "test.png")
        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertEquals(PacketFormat.PayloadType.IMAGE_PNG, decodeResult.payloadType)
    }

    @Test
    fun `round trip with APK payload type`() {
        val payload = byteArrayOf(0x50.toByte(), 0x4B.toByte(), 0x03.toByte(), 0x04.toByte())
        val encodeResult = encoder.encode(payload, "application/vnd.android.package-archive", "app.apk")
        val decodeResult = decoder.decode(encodeResult.stegoText)
        assertEquals(PacketFormat.PayloadType.ANDROID_APK, decodeResult.payloadType)
    }
}
