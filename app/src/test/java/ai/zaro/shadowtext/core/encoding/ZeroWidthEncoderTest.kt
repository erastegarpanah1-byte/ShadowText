package ai.zaro.shadowtext.core.encoding

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ZeroWidthEncoderTest {

    private lateinit var encoder: ZeroWidthEncoder

    @Before
    fun setUp() {
        encoder = ZeroWidthEncoder()
    }

    @Test
    fun `encode empty byte array produces empty string`() {
        val result = encoder.encode(ByteArray(0))
        assertEquals("", result)
        val decoded = encoder.decode(result)
        assertEquals(0, decoded.size)
    }

    @Test
    fun `round trip single byte`() {
        val original = byteArrayOf(0x42.toByte())
        val encoded = encoder.encode(original)
        assertTrue(encoded.isNotEmpty())
        val decoded = encoder.decode(encoded)
        assertArrayEquals(original, decoded)
    }

    @Test
    fun `round trip multiple bytes`() {
        val original = "Hello, ShadowText!".toByteArray(Charsets.UTF_8)
        val encoded = encoder.encode(original)
        assertTrue(encoded.isNotEmpty())
        val decoded = encoder.decode(encoded)
        assertArrayEquals(original, decoded)
    }

    @Test
    fun `round trip all byte values`() {
        val original = ByteArray(256) { it.toByte() }
        val encoded = encoder.encode(original)
        val decoded = encoder.decode(encoded)
        assertArrayEquals(original, decoded)
    }

    @Test
    fun `round trip binary data`() {
        val original = byteArrayOf(
            0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
            0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x0D.toByte(),
            0x49.toByte(), 0x48.toByte(), 0x44.toByte(), 0x52.toByte(),
        )
        val encoded = encoder.encode(original)
        val decoded = encoder.decode(encoded)
        assertArrayEquals(original, decoded)
    }

    @Test
    fun `round trip large payload`() {
        val original = ByteArray(10_000) { (it % 256).toByte() }
        val encoded = encoder.encode(original)
        val decoded = encoder.decode(encoded)
        assertArrayEquals(original, decoded)
    }

    @Test
    fun `decode works on mixed visible and invisible text`() {
        val original = byteArrayOf(0x01.toByte(), 0x02.toByte(), 0x03.toByte())
        val invisible = encoder.encode(original)
        val mixed = "Hello world, this is visible text." + invisible + "More visible text."
        val decoded = encoder.decode(mixed)
        assertArrayEquals(original, decoded)
    }

    @Test
    fun `extractInvisible isolates invisible characters`() {
        val invisible = encoder.encode(byteArrayOf(0x7F.toByte()))
        val mixed = "The quick brown fox.${invisible}End."
        val extracted = encoder.extractInvisible(mixed)
        extracted.forEach { ch ->
            assertTrue(
                "Character U+%04X should be invisible".format(ch.code),
                ch.code in listOf(0x200B, 0x200C, 0x200D, 0xFEFF, 0x2063)
            )
        }
    }

    @Test
    fun `containsEncodedData detects payload`() {
        val invisible = encoder.encode(byteArrayOf(0x42.toByte()))
        val mixed = "Visible text." + invisible + "More text."
        assertTrue(encoder.containsEncodedData(mixed))
    }

    @Test
    fun `containsEncodedData returns false for plain text`() {
        assertFalse(encoder.containsEncodedData("Just ordinary text."))
    }

    @Test
    fun `containsEncodedData returns false for empty string`() {
        assertFalse(encoder.containsEncodedData(""))
    }

    @Test(expected = EncodingException::class)
    fun `decode throws on plain text`() {
        encoder.decode("No invisible characters here.")
    }

    @Test
    fun `encode produces only invisible characters`() {
        val encoded = encoder.encode(byteArrayOf(0x00.toByte(), 0xFF.toByte()))
        val visibleChars = encoded.filter { it.code !in listOf(0x200B, 0x200C, 0x200D, 0xFEFF, 0x2063) }
        assertEquals("Should not contain visible chars, but found: '$visibleChars'", 0, visibleChars.length)
    }

    @Test
    fun `encodedCharCount is correct`() {
        val byteCount = 100
        val charCount = encoder.encodedCharCount(byteCount)
        val expected = byteCount * 4 + 4 + 4 + 2
        assertEquals(expected, charCount)
    }

    @Test
    fun `name and identifier are set`() {
        assertEquals("Zero-Width Characters", encoder.name)
        assertEquals("zwc", encoder.identifier)
        assertEquals(2, encoder.bitsPerChar)
    }

    @Test
    fun `round trip with zero bytes`() {
        val original = ByteArray(100)
        val encoded = encoder.encode(original)
        val decoded = encoder.decode(encoded)
        assertArrayEquals(original, decoded)
    }

    @Test
    fun `round trip with max bytes`() {
        val original = ByteArray(100) { 0xFF.toByte() }
        val encoded = encoder.encode(original)
        val decoded = encoder.decode(encoded)
        assertArrayEquals(original, decoded)
    }
}
