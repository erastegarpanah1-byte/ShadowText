package ai.zaro.shadowtext.core.format

/**
 * Binary packet format for ShadowText payloads.
 *
 * Layout (all fields are little-endian):
 * Magic Number (4B) | Version (2B) | Flags (1B) | Payload Type (1B)
 * | Payload Size (8B) | Metadata Len (4B) | Reserved (4B) | Metadata (NB)
 * | Payload (MB) | Checksum (4B, CRC32)
 */
object PacketFormat {

    const val MAGIC = 0x53544458  // "STDX" in little-endian
    const val CURRENT_VERSION: Short = 1

    const val MAGIC_SIZE = 4
    const val VERSION_SIZE = 2
    const val FLAGS_SIZE = 1
    const val PAYLOAD_TYPE_SIZE = 1
    const val PAYLOAD_SIZE_FIELD = 8
    const val METADATA_LEN_SIZE = 4
    const val RESERVED_SIZE = 4
    const val HEADER_SIZE = MAGIC_SIZE + VERSION_SIZE + FLAGS_SIZE +
            PAYLOAD_TYPE_SIZE + PAYLOAD_SIZE_FIELD + METADATA_LEN_SIZE + RESERVED_SIZE
    const val CHECKSUM_SIZE = 4
    const val TOTAL_MIN_SIZE = HEADER_SIZE + CHECKSUM_SIZE

    object Flags {
        const val NONE: Byte = 0x00
    }

    object PayloadType {
        const val RAW_BYTES: Byte = 0x00
        const val PLAIN_TEXT: Byte = 0x01
        const val IMAGE_PNG: Byte = 0x10
        const val IMAGE_JPEG: Byte = 0x11
        const val IMAGE_WEBP: Byte = 0x12
        const val IMAGE_GIF: Byte = 0x13
        const val IMAGE_BMP: Byte = 0x14
        const val IMAGE_SVG: Byte = 0x15
        const val IMAGE_OTHER: Byte = 0x1F
        const val VIDEO_MP4: Byte = 0x20
        const val VIDEO_OTHER: Byte = 0x2F
        const val AUDIO_MP3: Byte = 0x30
        const val AUDIO_WAV: Byte = 0x31
        const val AUDIO_OGG: Byte = 0x32
        const val AUDIO_OTHER: Byte = 0x3F
        const val DOCUMENT_PDF: Byte = 0x40
        const val ARCHIVE_ZIP: Byte = 0x50
        const val ARCHIVE_OTHER: Byte = 0x5F
        const val ANDROID_APK: Byte = 0x60
        const val UNKNOWN: Byte = 0x7F

        fun fromMimeType(mimeType: String?): Byte = when {
            mimeType == null -> UNKNOWN
            mimeType.startsWith("text/plain") -> PLAIN_TEXT
            mimeType == "image/png" -> IMAGE_PNG
            mimeType == "image/jpeg" -> IMAGE_JPEG
            mimeType == "image/webp" -> IMAGE_WEBP
            mimeType == "image/gif" -> IMAGE_GIF
            mimeType == "image/bmp" -> IMAGE_BMP
            mimeType == "image/svg+xml" -> IMAGE_SVG
            mimeType.startsWith("image/") -> IMAGE_OTHER
            mimeType == "video/mp4" -> VIDEO_MP4
            mimeType.startsWith("video/") -> VIDEO_OTHER
            mimeType == "audio/mpeg" -> AUDIO_MP3
            mimeType == "audio/wav" -> AUDIO_WAV
            mimeType == "audio/ogg" -> AUDIO_OGG
            mimeType.startsWith("audio/") -> AUDIO_OTHER
            mimeType == "application/pdf" -> DOCUMENT_PDF
            mimeType == "application/zip" -> ARCHIVE_ZIP
            mimeType.startsWith("application/") && mimeType.contains("zip") -> ARCHIVE_ZIP
            mimeType == "application/vnd.android.package-archive" -> ANDROID_APK
            else -> UNKNOWN
        }

        fun toLabel(type: Byte): String = when (type) {
            RAW_BYTES -> "Raw Bytes"
            PLAIN_TEXT -> "Plain Text"
            IMAGE_PNG -> "PNG Image"
            IMAGE_JPEG -> "JPEG Image"
            IMAGE_WEBP -> "WebP Image"
            IMAGE_GIF -> "GIF Image"
            IMAGE_BMP -> "BMP Image"
            IMAGE_SVG -> "SVG Image"
            IMAGE_OTHER -> "Image"
            VIDEO_MP4 -> "MP4 Video"
            VIDEO_OTHER -> "Video"
            AUDIO_MP3 -> "MP3 Audio"
            AUDIO_WAV -> "WAV Audio"
            AUDIO_OGG -> "OGG Audio"
            AUDIO_OTHER -> "Audio"
            DOCUMENT_PDF -> "PDF Document"
            ARCHIVE_ZIP -> "ZIP Archive"
            ARCHIVE_OTHER -> "Archive"
            ANDROID_APK -> "Android APK"
            UNKNOWN -> "Unknown"
            else -> "Unknown (0x%02X)".format(type)
        }
    }
}
