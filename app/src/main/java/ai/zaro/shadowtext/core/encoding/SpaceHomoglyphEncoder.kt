package ai.zaro.shadowtext.core.encoding

/**
 * Encoder that replaces ordinary spaces with 8 visually-identical
 * Unicode whitespace homoglyphs. Each space encodes 3 bits of data.
 *
 * The 8 space homoglyphs (all render as "a space"):
 *   000 → U+0020  Space
 *   001 → U+00A0  No-Break Space
 *   010 → U+1680  Ogham Space Mark
 *   011 → U+2000  En Quad
 *   100 → U+2001  Em Quad
 *   101 → U+2002  En Space
 *   110 → U+2003  Em Space
 *   111 → U+2004  Three-Per-Em Space
 *
 * Protocol (fixed 3-bit wide slots):
 *   [0..7]     Magic: 0xA55A5A (8 slots = 24 bits)
 *   [8..11]    Payload length in bytes (4 slots = 12 bits, max 4095)
 *   [12..]     Payload data, 1 byte = 3 slots (8 bits → 3×3bit with LSB padding)
 *
 * We replace EXISTING spaces in the cover text — never add new characters.
 * Text with replaced spaces looks completely natural to human eyes.
 */
class SpaceHomoglyphEncoder : InvisibleEncoder {

    override val name = "Space Homoglyph"
    override val identifier = "spgh"
    override val bitsPerChar = 3

    companion object {
        // 8 visually-identical whitespace homoglyphs
        private val GLYPHS = charArrayOf(
            '\u0020', '\u00A0', '\u1680', '\u2000',
            '\u2001', '\u2002', '\u2003', '\u2004',
        )
        private val CHAR_TO_BITS: Map<Char, Int> =
            GLYPHS.mapIndexed { i, c -> c to i }.toMap()

        private const val MAGIC = 0xA55A5A  // 24 bits
        private const val MAGIC_SLOTS = 8   // 24b ÷ 3b/slot
        private const val LEN_SLOTS = 4     // 12 bits ÷ 3b/slot → max 4095 bytes
    }

    override fun encode(bytes: ByteArray): String {
        if (bytes.isEmpty()) return ""

        val bits = BitWriter()
        // Magic: 24 bits
        bits.write24(MAGIC)
        // Length: 12 bits
        bits.write12(bytes.size)
        // Payload: each byte = 8 bits → encoded as 3 triplets (last triplet's 3rd bit is padding)
        for (b in bytes) {
            val v = b.toInt() and 0xFF
            bits.writeTriplet(v)
        }

        val sb = StringBuilder(bits.size() / 3)
        bits.forEachTriplet { t -> sb.append(GLYPHS[t and 0x07]) }
        return sb.toString()
    }

    override fun decode(encoded: String): ByteArray {
        val inv = extractInvisible(encoded)
        if (inv.isEmpty()) throw EncodingException("No encoded data found")

        val slots = inv.map { CHAR_TO_BITS[it] ?: throw EncodingException("Unknown glyph: U+%04X".format(it.code)) }
        val reader = BitReader(slots)
        if (reader.size() < MAGIC_SLOTS + LEN_SLOTS)
            throw EncodingException("Payload too short")

        // Magic
        val magic = reader.read24()
        if (magic != MAGIC) throw EncodingException("Magic mismatch: 0x%06X".format(magic))

        // Length
        val len = reader.read12()
        if (len < 0 || len > 4096)
            throw EncodingException("Invalid payload length: $len")

        // Payload
        val bytes = ByteArray(len)
        for (i in 0 until len) {
            bytes[i] = reader.readByte().toByte()
        }
        return bytes
    }

    override fun extractInvisible(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            if (ch in CHAR_TO_BITS) sb.append(ch)
        }
        return sb.toString()
    }

    override fun containsEncodedData(text: String): Boolean {
        val inv = extractInvisible(text)
        val slots = inv.mapNotNull { CHAR_TO_BITS[it] }
        if (slots.size < MAGIC_SLOTS) return false
        val reader = BitReader(slots)
        return try {
            reader.read24() == MAGIC
        } catch (_: Exception) {
            false
        }
    }

    override fun encodedCharCount(byteCount: Int): Int =
        MAGIC_SLOTS + LEN_SLOTS + byteCount * 3

    fun embed(carrierText: String, invisible: String): String {
        val invChars = invisible.toCharArray()
        var invIdx = 0
        val sb = StringBuilder()
        for (ch in carrierText) {
            if (ch == ' ' && invIdx < invChars.size) {
                sb.append(invChars[invIdx++])
            } else {
                sb.append(ch)
            }
        }
        while (invIdx < invChars.size) {
            sb.append(invChars[invIdx++])
        }
        return sb.toString()
    }
}

/** Writes bits grouped as 3-bit triplets. */
private class BitWriter {
    private var accumulator = 0L
    private var bitCount = 0
    private val triplets = mutableListOf<Int>()

    fun write24(value: Int) { write(value.toLong() and 0xFFFFFF, 24) }
    fun write12(value: Int) { write(value.toLong() and 0x0FFF, 12) }

    /** Encode one byte as 8 bits + padding → 3 triplets (9 bits total) */
    fun writeTriplet(byte: Int) {
        // Write 8 bits of data + 1 padding bit (0)
        write(byte.toLong() and 0xFF, 8)
        flushTriplets()
    }

    fun size() = triplets.size * 3

    fun forEachTriplet(action: (Int) -> Unit) { triplets.forEach(action) }

    private fun write(bits: Long, count: Int) {
        accumulator = (accumulator shl count) or bits
        bitCount += count
        flushTriplets()
    }

    private fun flushTriplets() {
        while (bitCount >= 3) {
            bitCount -= 3
            triplets.add(((accumulator shr bitCount) and 0x07).toInt())
        }
    }
}

/** Reads bits from 3-bit triplet slots. */
private class BitReader(private val slots: List<Int>) {
    private var pos = 0
    private var bitBuf = 0
    private var bitsAvail = 0

    fun size() = slots.size

    fun read24(): Int = readBits(24)
    fun read12(): Int = readBits(12)

    fun readByte(): Int {
        if (bitsAvail < 8) refill()
        bitsAvail -= 8
        return (bitBuf shr bitsAvail) and 0xFF
    }

    private fun readBits(count: Int): Int {
        var result = 0
        for (i in 0 until count) {
            if (bitsAvail == 0) refill()
            bitsAvail--
            result = (result shl 1) or ((bitBuf shr bitsAvail) and 1)
        }
        return result
    }

    private fun refill() {
        if (pos >= slots.size) throw EncodingException("Unexpected end of data")
        bitBuf = (bitBuf shl 3) or slots[pos]
        bitsAvail += 3
        pos++
    }
}
