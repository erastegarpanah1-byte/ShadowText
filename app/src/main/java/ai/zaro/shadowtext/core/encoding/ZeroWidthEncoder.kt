package ai.zaro.shadowtext.core.encoding

class ZeroWidthEncoder : InvisibleEncoder {

    override val name = "Zero-Width Characters"
    override val identifier = "zwc"
    override val bitsPerChar = 2

    companion object {
        private const val ZWSP = '​'
        private const val ZWNJ = '‌'
        private const val ZWJ  = '‍'
        private const val BOM  = '﻿'
        val ALL_CHARS = setOf(ZWSP, ZWNJ, ZWJ, BOM)
    }

    override fun encode(bytes: ByteArray): String {
        if (bytes.isEmpty()) return ""
        val sb = StringBuilder()
        encodeInt(sb, bytes.size)
        for (byte in bytes) {
            val b = byte.toInt() and 0xFF
            sb.append(bitsToChar((b shr 6) and 0x03))
            sb.append(bitsToChar((b shr 4) and 0x03))
            sb.append(bitsToChar((b shr 2) and 0x03))
            sb.append(bitsToChar(b and 0x03))
        }
        return sb.toString()
    }

    override fun decode(encoded: String): ByteArray {
        val invisible = extractInvisible(encoded)
        if (invisible.length < 8) throw EncodingException("No encoded data found")
        var idx = 0
        val chars = invisible.toCharArray()
        val len = decodeInt(chars, idx)
        idx += 8
        if (len < 0 || len > 100_000_000 || idx + len * 4 > chars.size)
            throw EncodingException("Invalid payload length: $len")
        val bytes = ByteArray(len)
        for (i in 0 until len) {
            val b0 = charToBits(chars[idx]) shl 6
            val b1 = charToBits(chars[idx + 1]) shl 4
            val b2 = charToBits(chars[idx + 2]) shl 2
            val b3 = charToBits(chars[idx + 3])
            bytes[i] = (b0 or b1 or b2 or b3).toByte()
            idx += 4
        }
        return bytes
    }

    override fun extractInvisible(text: String): String = text.filter { it in ALL_CHARS }
    override fun containsEncodedData(text: String): Boolean = extractInvisible(text).length >= 8
    override fun encodedCharCount(byteCount: Int): Int = 8 + byteCount * 4

    private fun encodeInt(sb: StringBuilder, value: Int) {
        for (shift in 24 downTo 0 step 8) {
            val b = (value shr shift) and 0xFF
            sb.append(bitsToChar((b shr 6) and 0x03))
            sb.append(bitsToChar((b shr 4) and 0x03))
            sb.append(bitsToChar((b shr 2) and 0x03))
            sb.append(bitsToChar(b and 0x03))
        }
    }

    private fun decodeInt(chars: CharArray, offset: Int): Int {
        var result = 0
        for (i in 0 until 4) {
            val idx = offset + i * 4
            val b0 = charToBits(chars[idx]) shl 6
            val b1 = charToBits(chars[idx + 1]) shl 4
            val b2 = charToBits(chars[idx + 2]) shl 2
            val b3 = charToBits(chars[idx + 3])
            result = (result shl 8) or (b0 or b1 or b2 or b3)
        }
        return result
    }

    private fun bitsToChar(bits: Int): Char = when (bits and 0x03) { 0 -> ZWSP; 1 -> ZWNJ; 2 -> ZWJ; else -> BOM }
    private fun charToBits(ch: Char): Int = when (ch) { ZWSP -> 0; ZWNJ -> 1; ZWJ -> 2; BOM -> 3; else -> throw EncodingException("Bad char") }
}
