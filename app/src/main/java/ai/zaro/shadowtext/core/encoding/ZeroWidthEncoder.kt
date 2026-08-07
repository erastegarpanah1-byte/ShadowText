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

        private val START_MARKER = charArrayOf(ZWSP, ZWNJ, ZWSP, ZWNJ, ZWSP, ZWNJ)
        private val END_MARKER   = charArrayOf(ZWSP, ZWJ, ZWSP, ZWJ, ZWSP, ZWJ)

        val ALL_CHARS = setOf(ZWSP, ZWNJ, ZWJ, BOM)
    }

    override fun encode(bytes: ByteArray): String {
        if (bytes.isEmpty()) return ""
        val sb = StringBuilder()
        START_MARKER.forEach { sb.append(it) }
        for (byte in bytes) {
            val b = byte.toInt() and 0xFF
            sb.append(bitsToChar((b shr 6) and 0x03))
            sb.append(bitsToChar((b shr 4) and 0x03))
            sb.append(bitsToChar((b shr 2) and 0x03))
            sb.append(bitsToChar(b and 0x03))
        }
        END_MARKER.forEach { sb.append(it) }
        return sb.toString()
    }

    override fun decode(encoded: String): ByteArray {
        val invisible = extractInvisible(encoded)
        if (invisible.length < 12) throw EncodingException("No encoded data found")
        val chars = invisible.toCharArray()
        var idx = skipMarker(chars, 0, START_MARKER)
        if (idx < 0) throw EncodingException("Start marker not found")
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
        if (bytes.isEmpty()) throw EncodingException("No payload between markers")
        return bytes.toByteArray()
    }

    override fun extractInvisible(text: String): String = text.filter { it in ALL_CHARS }

    override fun containsEncodedData(text: String): Boolean {
        val invisible = extractInvisible(text)
        return invisible.length >= 12 && invisible.contains(String(START_MARKER)) && invisible.contains(String(END_MARKER))
    }

    override fun encodedCharCount(byteCount: Int): Int = byteCount * 4 + 12

    private fun bitsToChar(bits: Int): Char = when (bits and 0x03) { 0 -> ZWSP; 1 -> ZWNJ; 2 -> ZWJ; else -> BOM }
    private fun charToBits(ch: Char): Int = when (ch) { ZWSP -> 0; ZWNJ -> 1; ZWJ -> 2; BOM -> 3; else -> throw EncodingException("Bad char") }

    private fun isMarker(chars: CharArray, offset: Int, marker: CharArray): Boolean {
        if (offset + marker.size > chars.size) return false
        for (i in marker.indices) if (chars[offset + i] != marker[i]) return false
        return true
    }

    private fun skipMarker(chars: CharArray, start: Int, marker: CharArray): Int {
        for (i in start..chars.size - marker.size) if (isMarker(chars, i, marker)) return i + marker.size
        return -1
    }
}
