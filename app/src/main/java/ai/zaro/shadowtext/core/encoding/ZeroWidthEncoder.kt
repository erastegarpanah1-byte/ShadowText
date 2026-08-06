package ai.zaro.shadowtext.core.encoding

class ZeroWidthEncoder : InvisibleEncoder {

    override val name = "Zero-Width Characters"
    override val identifier = "zwc"
    override val bitsPerChar = 2

    companion object {
        const val SENTINEL_PREFIX = "\u2063"
        const val SENTINEL_SUFFIX = "\u2063"
        private const val ZWSP = '\u200B'
        private const val ZWNJ = '\u200C'
        private const val ZWJ  = '\u200D'
        private const val BOM  = '\uFEFF'
        private val START_MARKER = charArrayOf(ZWSP, ZWSP, ZWSP, ZWNJ)
        private val END_MARKER   = charArrayOf(ZWSP, ZWSP, ZWSP, ZWJ)
        val ALL_CHARS = setOf(ZWSP, ZWNJ, ZWJ, BOM, '\u2063')
    }

    override fun encode(bytes: ByteArray): String {
        if (bytes.isEmpty()) return ""
        val sb = StringBuilder()
        sb.append(SENTINEL_PREFIX)
        START_MARKER.forEach { sb.append(it) }
        for (byte in bytes) {
            val b = byte.toInt() and 0xFF
            sb.append(bitsToChar((b shr 6) and 0x03))
            sb.append(bitsToChar((b shr 4) and 0x03))
            sb.append(bitsToChar((b shr 2) and 0x03))
            sb.append(bitsToChar(b and 0x03))
        }
        END_MARKER.forEach { sb.append(it) }
        sb.append(SENTINEL_SUFFIX)
        return sb.toString()
    }

    override fun decode(encoded: String): ByteArray {
        val invisible = extractInvisible(encoded)
        if (invisible.isEmpty()) return ByteArray(0)
        var idx = 0
        val chars = invisible.toCharArray()
        idx = skipMarker(chars, idx, START_MARKER)
        if (idx < 0) throw EncodingException("Start marker not found in encoded data")
        val bytes = mutableListOf<Byte>()
        while (idx + 3 < chars.size) {
            if (isMarker(chars, idx, END_MARKER)) break
            val b0 = charToBits(chars[idx]) shl 6
            val b1 = charToBits(chars[idx + 1]) shl 4
            val b2 = charToBits(chars[idx + 2]) shl 2
            val b3 = charToBits(chars[idx + 3])
            bytes.add((b0 or b1 or b2 or b3).toByte())
            idx += 4
        }
        if (bytes.isEmpty()) throw EncodingException("No payload data found between markers")
        return bytes.toByteArray()
    }

    override fun extractInvisible(text: String): String {
        return text.filter { it in ALL_CHARS }
    }

    override fun containsEncodedData(text: String): Boolean {
        val invisible = extractInvisible(text)
        if (invisible.length < START_MARKER.size + END_MARKER.size) return false
        return invisible.contains(String(START_MARKER)) &&
                invisible.contains(String(END_MARKER))
    }

    override fun encodedCharCount(byteCount: Int): Int {
        return byteCount * 4 + START_MARKER.size + END_MARKER.size + 2
    }

    private fun bitsToChar(bits: Int): Char = when (bits and 0x03) {
        0 -> ZWSP
        1 -> ZWNJ
        2 -> ZWJ
        3 -> BOM
        else -> ZWSP
    }

    private fun charToBits(ch: Char): Int = when (ch) {
        ZWSP -> 0
        ZWNJ -> 1
        ZWJ  -> 2
        BOM  -> 3
        else -> throw EncodingException("Unexpected character: U+%04X".format(ch.code))
    }

    private fun isMarker(chars: CharArray, offset: Int, marker: CharArray): Boolean {
        if (offset + marker.size > chars.size) return false
        for (i in marker.indices) {
            if (chars[offset + i] != marker[i]) return false
        }
        return true
    }

    private fun skipMarker(chars: CharArray, start: Int, marker: CharArray): Int {
        for (i in start until chars.size - marker.size + 1) {
            if (isMarker(chars, i, marker)) return i + marker.size
        }
        return -1
    }
}
