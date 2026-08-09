package ai.zaro.shadowtext.core.encoding

/**
 * Encodes arbitrary bytes as invisible Unicode Variation Selectors.
 *
 * Uses the full 256 Variation Selectors range:
 *   VS1–VS16   U+FE00–U+FE0F   (bytes 0x00–0x0F)
 *   VS17–VS256 U+E0100–U+E01EF (bytes 0x10–0xFF)
 *
 * Each selector encodes ONE FULL BYTE — no framing bits, no loss.
 * Selectors attach after visible characters; they have zero glyph width
 * and are classified as "Default Ignorable" by Unicode, surviving
 * copy-paste, transit, and most sanitization pipelines.
 *
 * Protocol (byte-aligned):
 *   Magic:  4 bytes = "SVS\0"  (0x53 0x56 0x53 0x00)
 *   Len:    4 bytes = payload length (big-endian u32)
 *   Data:   N bytes = payload
 */
class VariationSelectorEncoder : InvisibleEncoder {

    override val name = "Variation Selectors"
    override val identifier = "vs256"
    override val bitsPerChar = 8

    companion object {
        private const val MAGIC = 0x53565300  // "SVS\0"
        private const val HEADER_SIZE = 8     // 4 magic + 4 length

        fun vsForByte(b: Int): Char =
            if (b < 16) (0xFE00 + b).toChar()
            else (0xE0100 + (b - 16)).toChar()

        fun byteForVs(ch: Char): Int {
            val cp = ch.code
            return when {
                cp in 0xFE00..0xFE0F -> cp - 0xFE00
                cp in 0xE0100..0xE01EF -> cp - 0xE0100 + 16
                else -> -1
            }
        }

        fun isVs(ch: Char): Boolean = byteForVs(ch) >= 0
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
        val bytes = encoded.mapNotNull { ch ->
            val b = byteForVs(ch); if (b >= 0) b else null
        }
        if (bytes.size < HEADER_SIZE) throw EncodingException("Too few selectors: ${bytes.size} < $HEADER_SIZE")
        var magic = 0
        for (i in 0..3) magic = (magic shl 8) or bytes[i]
        if (magic != MAGIC) throw EncodingException("Magic mismatch: 0x%08X".format(magic))
        var len = 0
        for (i in 4..7) len = (len shl 8) or bytes[i]
        if (len < 0 || len > 50_000_000) throw EncodingException("Invalid payload length: $len")
        if (bytes.size < HEADER_SIZE + len) throw EncodingException("Truncated payload")
        return ByteArray(len) { i -> bytes[HEADER_SIZE + i].toByte() }
    }

    override fun extractInvisible(text: String): String {
        val sb = StringBuilder()
        for (ch in text) if (isVs(ch)) sb.append(ch)
        return sb.toString()
    }

    override fun containsEncodedData(text: String): Boolean {
        val inv = extractInvisible(text)
        if (inv.length < HEADER_SIZE) return false
        return try {
            val bytes = inv.map { ch -> byteForVs(ch) }
            var magic = 0
            for (i in 0..3) magic = (magic shl 8) or bytes[i]
            magic == MAGIC
        } catch (_: Exception) { false }
    }

    override fun encodedCharCount(byteCount: Int): Int = HEADER_SIZE + byteCount

    /** Interleave invisible selectors after each visible char of carrier text. */
    fun embed(carrierText: String, invisible: String): String {
        val invIter = invisible.iterator()
        val sb = StringBuilder()
        for (ch in carrierText) {
            sb.append(ch)
            if (invIter.hasNext()) sb.append(invIter.next())
        }
        while (invIter.hasNext()) sb.append(invIter.next())
        return sb.toString()
    }
}
