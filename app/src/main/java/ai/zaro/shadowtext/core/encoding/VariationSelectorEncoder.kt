package ai.zaro.shadowtext.core.encoding

/**
 * Encodes arbitrary bytes as invisible Unicode Variation Selectors.
 *
 * Uses the full 256 Variation Selectors range:
 *   VS1–VS16   U+FE00–U+FE0F   (bytes 0x00–0x0F)
 *   VS17–VS256 U+E0100–U+E01EF (bytes 0x10–0xFF)
 *
 * Each selector encodes ONE FULL BYTE.
 * CRITICAL: VS17+ are supplementary Unicode (> U+FFFF) → UTF-16 surrogate pairs.
 * All iteration MUST use codePointAt, NOT char-by-char iteration.
 */
class VariationSelectorEncoder : InvisibleEncoder {

    override val name = "Variation Selectors"
    override val identifier = "vs256"
    override val bitsPerChar = 8

    companion object {
        const val MAGIC = 0x53565300  // "SVS\0"
        const val HEADER_SIZE = 8     // 4 magic + 4 length

        fun vsForByte(b: Int): Char =
            if (b < 16) (0xFE00 + b).toChar()
            else (0xE0100 + (b - 16)).toChar()

        /** Returns byte value (0-255) if cp is a VS code point, -1 otherwise. */
        private fun byteForCp(cp: Int): Int = when {
            cp in 0xFE00..0xFE0F   -> cp - 0xFE00
            cp in 0xE0100..0xE01EF -> cp - 0xE0100 + 16
            else -> -1
        }

        fun isVsCp(cp: Int): Boolean = byteForCp(cp) >= 0
    }

    override fun encode(bytes: ByteArray): String {
        val sb = StringBuilder(HEADER_SIZE + bytes.size)
        for (shift in 24 downTo 0 step 8)
            sb.append(vsForByte((MAGIC shr shift) and 0xFF))
        val len = bytes.size
        for (shift in 24 downTo 0 step 8)
            sb.append(vsForByte((len shr shift) and 0xFF))
        for (b in bytes)
            sb.append(vsForByte(b.toInt() and 0xFF))
        return sb.toString()
    }

    override fun decode(encoded: String): ByteArray {
        val bytes = extractBytes(encoded)
        if (bytes.size < HEADER_SIZE)
            throw EncodingException("Too few selectors: ${bytes.size} < $HEADER_SIZE")
        var magic = 0
        for (i in 0..3) magic = (magic shl 8) or bytes[i]
        if (magic != MAGIC)
            throw EncodingException("Magic mismatch: 0x%08X".format(magic))
        var len = 0
        for (i in 4..7) len = (len shl 8) or bytes[i]
        if (len < 0 || len > 50_000_000)
            throw EncodingException("Invalid payload length: $len")
        if (bytes.size < HEADER_SIZE + len)
            throw EncodingException("Truncated payload: expected $len got ${bytes.size - HEADER_SIZE}")
        return ByteArray(len) { i -> bytes[HEADER_SIZE + i].toByte() }
    }

    override fun extractInvisible(text: String): String {
        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            if (isVsCp(cp)) sb.appendCodePoint(cp)
            i += Character.charCount(cp)
        }
        return sb.toString()
    }

    override fun containsEncodedData(text: String): Boolean {
        val bytes = extractBytes(text)
        if (bytes.size < HEADER_SIZE) return false
        var magic = 0
        for (i in 0..3) magic = (magic shl 8) or bytes[i]
        return magic == MAGIC
    }

    override fun encodedCharCount(byteCount: Int): Int = HEADER_SIZE + byteCount

    /** Interleave invisible selectors after each visible character. */
    fun embed(carrierText: String, invisible: String): String {
        // invisible is already a proper code-point string, so char iteration is fine
        // because StringBuilder.append() handles surrogates correctly when we
        // append the original chars. But to be safe we iterate by code points.
        var invIdx = 0
        val sb = StringBuilder()
        var ci = 0
        while (ci < carrierText.length) {
            val ccp = carrierText.codePointAt(ci)
            sb.appendCodePoint(ccp)
            ci += Character.charCount(ccp)
            // Append one VS from invisible
            if (invIdx < invisible.length) {
                val vcp = invisible.codePointAt(invIdx)
                sb.appendCodePoint(vcp)
                invIdx += Character.charCount(vcp)
            }
        }
        while (invIdx < invisible.length) {
            val vcp = invisible.codePointAt(invIdx)
            sb.appendCodePoint(vcp)
            invIdx += Character.charCount(vcp)
        }
        return sb.toString()
    }

    /** Extract all VS bytes from text (code-point-safe). */
    private fun extractBytes(text: String): List<Int> {
        val result = mutableListOf<Int>()
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            val b = byteForCp(cp)
            if (b >= 0) result.add(b)
            i += Character.charCount(cp)
        }
        return result
    }
}
