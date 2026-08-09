package ai.zaro.shadowtext.core.encoding

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VariationSelectorEncoderTest {
    private lateinit var e: VariationSelectorEncoder

    @Before fun setUp() { e = VariationSelectorEncoder() }

    @Test fun nameAndId() {
        assertEquals("Variation Selectors", e.name)
        assertEquals("vs256", e.identifier)
        assertEquals(8, e.bitsPerChar)
    }

    @Test fun roundTripSingleByte() {
        assertArrayEquals(byteArrayOf(0x42), e.decode(e.encode(byteArrayOf(0x42))))
    }

    @Test fun roundTripAllBytes() {
        val orig = ByteArray(256) { it.toByte() }
        assertArrayEquals(orig, e.decode(e.encode(orig)))
    }

    @Test fun roundTripHello() {
        val orig = "hello world".toByteArray(Charsets.UTF_8)
        assertArrayEquals(orig, e.decode(e.encode(orig)))
    }

    @Test fun roundTripLarge() {
        val orig = ByteArray(10_000) { (it % 256).toByte() }
        assertArrayEquals(orig, e.decode(e.encode(orig)))
    }

    @Test fun allEncodedAreVs() {
        val enc = e.encode(byteArrayOf(0x00, 0x0F, 0x10, 0xFF.toByte()))
        var i = 0
        while (i < enc.length) {
            val cp = enc.codePointAt(i)
            assertTrue("U+${cp.toString(16)} not VS", VariationSelectorEncoder.isVsCp(cp))
            i += Character.charCount(cp)
        }
    }

    @Test fun extractFromMixed() {
        val enc = e.encode(byteArrayOf(0x42, 0x13, 0xFF.toByte()))
        val mixed = "Hello World" + enc + "more text"
        val decoded = e.decode(e.extractInvisible(mixed))
        assertEquals(3, decoded.size)
    }

    @Test fun containsEncodedTrue() {
        val enc = e.encode(byteArrayOf(0x42))
        assertTrue(e.containsEncodedData(enc))
        assertTrue(e.containsEncodedData("visible text" + enc + "more"))
    }

    @Test fun containsEncodedFalse() {
        assertFalse(e.containsEncodedData("Hello World"))
    }

    @Test(expected = EncodingException::class)
    fun decodeThrowsOnPlain() { e.decode("Hello World") }

    @Test(expected = EncodingException::class)
    fun decodeThrowsOnEmpty() { e.decode("") }

    @Test fun embedPreservesCover() {
        val inv = e.encode("hello".toByteArray(Charsets.UTF_8))
        val stego = e.embed("ABCD", inv)
        val visible: String = buildString {
            var i = 0
            while (i < stego.length) {
                val cp = stego.codePointAt(i)
                if (!VariationSelectorEncoder.isVsCp(cp)) appendCodePoint(cp)
                i += Character.charCount(cp)
            }
        }
        assertEquals("ABCD", visible)
    }

    @Test fun emptyPayload() {
        val enc = e.encode(ByteArray(0))
        var cpCount = 0; var i = 0
        while (i < enc.length) {
            enc.codePointAt(i); cpCount++
            i += Character.charCount(enc.codePointAt(i))
        }
        assertEquals(8, cpCount)
        assertEquals(0, e.decode(enc).size)
    }
}
