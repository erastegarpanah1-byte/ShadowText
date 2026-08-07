package ai.zaro.shadowtext.core.encoding

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ZeroWidthEncoderTest {

    private lateinit var encoder: ZeroWidthEncoder

    @Before fun setUp() { encoder = ZeroWidthEncoder() }

    @Test fun `round trip single byte`() {
        val original = byteArrayOf(0x42.toByte())
        assertArrayEquals(original, encoder.decode(encoder.encode(original)))
    }

    @Test fun `round trip multiple bytes`() {
        val original = "Hello, ShadowText!".toByteArray(Charsets.UTF_8)
        assertArrayEquals(original, encoder.decode(encoder.encode(original)))
    }

    @Test fun `round trip all byte values`() {
        val original = ByteArray(256) { it.toByte() }
        assertArrayEquals(original, encoder.decode(encoder.encode(original)))
    }

    @Test fun `round trip binary data`() {
        val original = byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())
        assertArrayEquals(original, encoder.decode(encoder.encode(original)))
    }

    @Test fun `round trip large payload`() {
        val original = ByteArray(10_000) { (it % 256).toByte() }
        assertArrayEquals(original, encoder.decode(encoder.encode(original)))
    }

    @Test fun `decode works on mixed text`() {
        val original = byteArrayOf(0x01.toByte(), 0x02.toByte(), 0x03.toByte())
        val mixed = "Hello" + encoder.encode(original) + "World"
        assertArrayEquals(original, encoder.decode(mixed))
    }

    @Test fun `containsEncodedData detects payload`() {
        assertTrue(encoder.containsEncodedData("x" + encoder.encode(byteArrayOf(0x42.toByte())) + "y"))
    }

    @Test fun `containsEncodedData false for plain text`() {
        assertFalse(encoder.containsEncodedData("Just ordinary text."))
    }

    @Test(expected = EncodingException::class)
    fun `decode throws on plain text`() {
        encoder.decode("No invisible characters here.")
    }

    @Test fun `encode produces only invisible characters`() {
        val encoded = encoder.encode(byteArrayOf(0x00.toByte(), 0xFF.toByte()))
        encoded.forEach { assertTrue(it.code in listOf(0x200B, 0x200C, 0x200D, 0xFEFF)) }
    }

    @Test fun `name and identifier`() {
        assertEquals("Zero-Width Characters", encoder.name)
        assertEquals("zwc", encoder.identifier)
        assertEquals(2, encoder.bitsPerChar)
    }
}
