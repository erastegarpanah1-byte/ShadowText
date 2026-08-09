package ai.zaro.shadowtext.core.engine

import ai.zaro.shadowtext.core.encoding.VariationSelectorEncoder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StegoEngineIntegrationTest {
    private lateinit var enc: StegoEncoder
    private lateinit var dec: StegoDecoder
    @Before fun setUp() {
        val vs = VariationSelectorEncoder()
        enc = StegoEncoder(vs)
        dec = StegoDecoder(listOf(vs))
    }
    @Test fun roundTripText() {
        val p = "Secret.".toByteArray(Charsets.UTF_8)
        val r = enc.encode(p, "text/plain", "s.txt", "Hello world.")
        assertArrayEquals(p, dec.decode(r.stegoText).payload)
    }
    @Test fun roundTripBinary() {
        val p = ByteArray(256) { (it % 256).toByte() }
        val r = enc.encode(p, null, null, "Cover text here.")
        assertArrayEquals(p, dec.decode(r.stegoText).payload)
    }
    @Test fun roundTripPersian() {
        val p = "سلام دنیا".toByteArray(Charsets.UTF_8)
        val r = enc.encode(p, "text/plain", "persian.txt", "متن پوششی")
        assertArrayEquals(p, dec.decode(r.stegoText).payload)
    }
    @Test fun roundTripLargePayload() {
        val p = ByteArray(10_000) { (it % 256).toByte() }
        val r = enc.encode(p, null, null, "Long cover text.")
        assertArrayEquals(p, dec.decode(r.stegoText).payload)
    }
    @Test fun detectFindsPayload() {
        val r = enc.encode("x".toByteArray(), null, null, "Test.")
        assertTrue(dec.detect(r.stegoText).hasHiddenPayload)
    }
    @Test fun detectFalsePlain() {
        assertFalse(dec.detect("Ordinary text.").hasHiddenPayload)
    }
    @Test fun stegoTextAppearsNormal() {
        val cover = "This is a normal message"
        val r = enc.encode("data".toByteArray(), "text/plain", "f.txt", cover)
        // Extract only visible (non-VS) code points
        val visible = buildString {
            var i = 0; while (i < r.stegoText.length) {
                val cp = r.stegoText.codePointAt(i)
                if (!VariationSelectorEncoder.isVsCp(cp)) appendCodePoint(cp)
                i += Character.charCount(cp)
            }
        }
        assertEquals(cover, visible)
    }
    @Test fun encodeResultHasEncodingScheme() {
        val r = enc.encode("x".toByteArray(), null, null, "Test.")
        assertEquals("vs256", r.encodingScheme)
    }
}
