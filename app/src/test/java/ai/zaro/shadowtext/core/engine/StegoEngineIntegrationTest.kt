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
        enc = StegoEncoder(zw)
        dec = StegoDecoder(listOf(zw))
    }
    @Test fun roundTripText() {
        val p = "Secret.".toByteArray(Charsets.UTF_8)
        val r = enc.encode(payload = p, mimeType = "text/plain", fileName = "s.txt", carrierText = "Hello world.")
        assertArrayEquals(p, dec.decode(r.stegoText).payload)
    }
    @Test fun roundTripBinary() {
        val p = ByteArray(256) { (it % 256).toByte() }
        val r = enc.encode(payload = p, carrierText = "Cover text here.")
        assertArrayEquals(p, dec.decode(r.stegoText).payload)
    }
    @Test fun detectFindsPayload() {
        val r = enc.encode(payload = "x".toByteArray(), carrierText = "Test.")
        assertTrue(dec.detect(r.stegoText).hasHiddenPayload)
    }
    @Test fun detectFalsePlain() {
        assertFalse(dec.detect("Ordinary text.").hasHiddenPayload)
    }
    @Test(expected = StegoException::class)
    fun decodeThrowsPlainText() { dec.decode("No data.") }
}
