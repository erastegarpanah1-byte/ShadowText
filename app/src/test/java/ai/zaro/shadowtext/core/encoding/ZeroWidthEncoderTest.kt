package ai.zaro.shadowtext.core.encoding

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ZeroWidthEncoderTest {
    private lateinit var e: ZeroWidthEncoder
    @Before fun setUp() { e = ZeroWidthEncoder() }

    @Test fun `round trip single byte`() {
        val o = byteArrayOf(0x42.toByte())
        assertArrayEquals(o, e.decode(e.encode(o)))
    }
    @Test fun `round trip all byte values`() {
        val o = ByteArray(256) { it.toByte() }
        assertArrayEquals(o, e.decode(e.encode(o)))
    }
    @Test fun `round trip large payload`() {
        val o = ByteArray(10_000) { (it % 256).toByte() }
        assertArrayEquals(o, e.decode(e.encode(o)))
    }
    @Test fun `decode works on mixed text`() {
        val o = byteArrayOf(0x01.toByte(), 0x02.toByte(), 0x03.toByte())
        val m = "Hello" + e.encode(o) + "World"
        assertArrayEquals(o, e.decode(m))
    }
    @Test fun `containsEncodedData detects`() {
        assertTrue(e.containsEncodedData("x" + e.encode(byteArrayOf(0x42.toByte())) + "y"))
    }
    @Test fun `containsEncodedData false plain`() {
        assertFalse(e.containsEncodedData("Hello"))
    }
    @Test(expected = EncodingException::class)
    fun `decode throws on plain text`() {
        e.decode("No invisible chars.")
    }
    @Test fun `encode only invisible chars`() {
        val encoded = e.encode(byteArrayOf(0x00.toByte(), 0xFF.toByte()))
        encoded.forEach { assertTrue(it.code in listOf(0x200B, 0x200C, 0x200D, 0xFEFF)) }
    }
}
