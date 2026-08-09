package ai.zaro.shadowtext.core.encoding

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class VariationSelectorEncoderTest {
    private lateinit var e: VariationSelectorEncoder

    @Before
    fun setUp() { e = VariationSelectorEncoder() }

    @Test fun `name and identifier`() {
        assertEquals("Variation Selectors", e.name)
        assertEquals("vs256", e.identifier)
        assertEquals(8, e.bitsPerChar)
    }

    @Test fun `encode single byte`() {
        val enc = e.encode(byteArrayOf(0x42))
        assertTrue(enc.length >= 9)
        assertArrayEquals(byteArrayOf(0x42), e.decode(enc))
    }

    @Test fun `encode all byte values 0-255`() {
        val orig = ByteArray(256) { it.toByte() }
        assertArrayEquals(orig, e.decode(e.encode(orig)))
    }

    @Test fun `encode hello round trip`() {
        val orig = "hello world".toByteArray(Charsets.UTF_8)
        assertArrayEquals(orig, e.decode(e.encode(orig)))
    }

    @Test fun `encode فارسی round trip`() {
        val orig = "سلام دنیا".toByteArray(Charsets.UTF_8)
        assertArrayEquals(orig, e.decode(e.encode(orig)))
    }

    @Test fun `encode large payload 10KB`() {
        val orig = ByteArray(10_000) { (it % 256).toByte() }
        assertArrayEquals(orig, e.decode(e.encode(orig)))
    }

    @Test fun `all encoded chars are variation selectors`() {
        val enc = e.encode(byteArrayOf(0x00, 0x42, 0xFF.toByte()))
        for (ch in enc) {
            val b = VariationSelectorEncoder.byteForVs(ch)
            assertTrue("U+${ch.code.toString(16)} is not a VS", b in 0..255)
        }
    }

    @Test fun `extract invisible from mixed text`() {
        val enc = e.encode(byteArrayOf(0x42, 0x13, 0xFF.toByte()))
        val mixed = "Hello World" + enc + "more text"
        val extracted = e.extractInvisible(mixed)
        val decoded = e.decode(extracted)
        assertEquals(3, decoded.size)
    }

    @Test fun `contains encoded data true`() {
        val enc = e.encode(byteArrayOf(0x42))
        assertTrue(e.containsEncodedData(enc))
        assertTrue(e.containsEncodedData("visible text" + enc + "more"))
    }

    @Test fun `contains encoded data false plain`() {
        assertFalse(e.containsEncodedData("Hello World"))
        assertFalse(e.containsEncodedData(""))
    }

    @Test(expected = EncodingException::class)
    fun `decode throws on plain text`() { e.decode("Hello World") }

    @Test(expected = EncodingException::class)
    fun `decode throws on empty`() { e.decode("") }

    @Test fun `embed interleaves selectors after each char`() {
        val cover = "ABCD"
        val inv = e.encode(byteArrayOf(0x01, 0x02))
        val stego = e.embed(cover, inv)
        val visible = stego.filter { !VariationSelectorEncoder.isVs(it) }
        assertEquals("ABCD", visible)
    }

    @Test fun `empty payload round trip`() {
        assertEquals(8, e.encode(ByteArray(0)).length)
        assertEquals(0, e.decode(e.encode(ByteArray(0))).size)
    }
}
