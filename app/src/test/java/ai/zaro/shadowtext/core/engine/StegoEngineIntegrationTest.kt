package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.ZeroWidthEncoder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StegoEngineIntegrationTest {

    private lateinit var encoder: StegoEncoder
    private lateinit var decoder: StegoDecoder

    @Before fun setUp() {
        val zw = ZeroWidthEncoder()
        encoder = StegoEncoder(zw, GeneratedCarrierTextProvider(3))
        decoder = StegoDecoder(listOf(zw))
    }

    @Test fun `full round trip small text`() {
        val payload = "The secret message.".toByteArray(Charsets.UTF_8)
        val r = encoder.encode(payload, "text/plain", "secret.txt")
        val d = decoder.decode(r.stegoText)
        assertArrayEquals(payload, d.payload)
    }

    @Test fun `full round trip binary payload`() {
        val payload = ByteArray(512) { (it % 256).toByte() }
        val r = encoder.encode(payload)
        assertArrayEquals(payload, decoder.decode(r.stegoText).payload)
    }

    @Test fun `full round trip no metadata`() {
        val payload = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        val r = encoder.encode(payload)
        assertArrayEquals(payload, decoder.decode(r.stegoText).payload)
    }

    @Test fun `detection finds hidden payload`() {
        val r = encoder.encode("x".toByteArray())
        assertTrue(decoder.detect(r.stegoText).hasHiddenPayload)
    }

    @Test fun `detection false for plain text`() {
        assertFalse(decoder.detect("Just ordinary text.").hasHiddenPayload)
    }

    @Test(expected = StegoException::class)
    fun `decode throws on plain text`() {
        decoder.decode("No hidden data.")
    }
}
