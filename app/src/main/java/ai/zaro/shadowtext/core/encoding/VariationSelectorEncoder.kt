package ai.zaro.shadowtext.core.encoding

/**
 * Encodes arbitrary bytes as invisible Unicode Variation Selectors.
 *
 * Uses the full 256 Variation Selectors range:
 *   VS1–VS16   U+FE00–U+FE0F   (bytes 0x00–0x0F)
 *   VS17–VS256 U+E0100–U+E01EF (bytes 0x10–0xFF)
 *
 * CRITICAL: Kotlin Char is 16-bit → cannot hold supplementary (>U+FFFF).
 * vsForByte returns String (surrogate pair for VS17+).
 * All encoding uses appendCodePoint, all decoding uses codePointAt.
 */
class VariationSelectorEncoder : InvisibleEncoder {

    override val name = "Variation Selectors"
    override val identifier = "vs256"
    override val bitsPerChar = 8

    companion object {
        const val MAGIC = 0x53565300  // "SVS\0"
        const val HEADER_SIZE = 8

        // ── code point helpers ──

        /** Returns the VS code point for a byte value (0-255). */
        fun vsCodePoint(b: Int): Int =
            if (b < 16) 0xFE00 + b else 0xE0100 + (b - 16)

        /** Returns byte value (0-255) if cp is a VS code point, -1 otherwise. */
        fun byteForCp(cp: Int): Int = when {
            cp in 0xFE00..0xFE0F   -> cp - 0xFE00
            cp in 0xE0100..0xE01EF -> cp - 0xE0100 + 16
            else -> -1
        }

        fun isVsCp(cp: Int): Boolean = byteForCp(cp) >= 0
    }

    // ── InvisibleEncoder interface ──

    override fun encode(bytes: ByteArray): String {
        val sb = StringBuilder(HEADER_SIZE + bytes.size)
        // Magic: 4 selectors
        for (shift in 24 downTo 0 step 8)
            sb.appendCodePoint(vsCodePoint((MAGIC shr shift) and 0xFF))
        // Length: 4 selectors (big-endian u32)
        val len = bytes.size
        for (shift in 24 downTo 0 step 8)
            sb.appendCodePoint(vsCodePoint((len shr shift) and 0xFF))
        // Payload: 1 selector per byte
        for (b in bytes)
            sb.appendCodePoint(vsCodePoint(b.toInt() and 0xFF))
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
            throw EncodingException("Truncated: expected $len got ${bytes.size - HEADER_SIZE}")
        return ByteArray(len) { i -> bytes[HEADER_SIZE + i].toByte() }
    }

    override fun extractInvisible(text: String): String {
        val sb = StringBuilder()
        forEachCp(text) { cp -> if (isVsCp(cp)) sb.appendCodePoint(cp) }
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

    /** Interleave invisible selectors after each code point of carrier text. */
    fun embed(carrierText: String, invisible: String): String {
        // Collect invisible code points
        val inv = mutableListOf<Int>()
        forEachCp(invisible) { cp -> if (isVsCp(cp)) inv.add(cp) }
        var invIdx = 0
        val sb = StringBuilder()
        forEachCp(carrierText) { cp ->
            sb.appendCodePoint(cp)
            if (invIdx < inv.size) {
                sb.appendCodePoint(inv[invIdx++])
            }
        }
        while (invIdx < inv.size) {
            sb.appendCodePoint(inv[invIdx++])
        }
        return sb.toString()
    }

    // ── private helpers ──

    /** Iterate code points of a string. */
    private fun forEachCp(text: String, action: (Int) -> Unit) {
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            action(cp)
            i += Character.charCount(cp)
        }
    }

    /** Extract all VS byte values from text. */
    private fun extractBytes(text: String): List<Int> {
        val result = mutableListOf<Int>()
        forEachCp(text) { cp ->
            val b = byteForCp(cp)
            if (b >= 0) result.add(b)
        }
        return result
    }
}
