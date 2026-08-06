package ai.zaro.shadowtext.core.encoding

interface InvisibleEncoder {
    val name: String
    val identifier: String
    val bitsPerChar: Int
    fun encode(bytes: ByteArray): String
    fun decode(encoded: String): ByteArray
    fun extractInvisible(text: String): String
    fun containsEncodedData(text: String): Boolean
    fun encodedCharCount(byteCount: Int): Int
}

class EncodingException(message: String, cause: Throwable? = null) :
    Exception(message, cause)
