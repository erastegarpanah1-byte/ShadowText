package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.ZeroWidthEncoder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StegoEngineIntegrationTest {
    private lateinit var enc: StegoEncoder
    private lateinit var dec: StegoDecoder
    @Before fun setUp() {
        val zw = ZeroWidthEncoder()
        enc = StegoEncoder(zw, GeneratedCarrierTextProvider(minSentences = 3))
        dec = StegoDecoder(listOf(zw))
    }
    @Test fun `round trip small text`() {
        val p = "Secret.".toByteArray(Charsets.UTF_8)
        assertArrayEquals(p, dec.decode(enc.encode(p, "text/plain", "s.txt").stegoText).payload)
    }
    @Test fun `round trip binary`() {
        val p = ByteArray(256) { (it % 256).toByte() }
        assertArrayEquals(p, dec.decode(enc.encode(p).stegoText).payload)
    }
    @Test fun `round trip no metadata`() {
        val p = byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte())
        assertArrayEquals(p, dec.decode(enc.encode(p).stegoText).payload)
    }
    @Test fun `detect finds payload`() {
        assertTrue(dec.detect(enc.encode("x".toByteArray()).stegoText).hasHiddenPayload)
    }
    @Test fun `detect false plain`() {
        assertFalse(dec.detect("Ordinary text.").hasHiddenPayload)
    }
    @Test(expected = StegoException::class)
    fun `decode throws plain text`() { dec.decode("No data.") }
}
