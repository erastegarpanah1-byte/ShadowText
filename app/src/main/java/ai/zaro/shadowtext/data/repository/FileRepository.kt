package ai.zaro.shadowtext.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun readUri(uri: Uri): Pair<ByteArray, Pair<String?, String?>> {
        val fileName: String?
        val mimeType: String?

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = if (nameIdx >= 0) cursor.getString(nameIdx) else null
            } else {
                fileName = null
            }
        } ?: run { fileName = null }

        mimeType = context.contentResolver.getType(uri)

        val bytes = context.contentResolver.openInputStream(uri)?.use { stream ->
            stream.readBytes()
        } ?: throw FileRepositoryException("Failed to open input stream for: $uri")

        return Pair(bytes, Pair(fileName, mimeType))
    }

    fun readFile(file: File): Pair<ByteArray, Pair<String?, String?>> {
        val fileName = file.name
        val extension = file.extension
        val mimeType = if (extension.isNotEmpty()) {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        } else null

        val bytes = file.readBytes()
        return Pair(bytes, Pair(fileName, mimeType))
    }

    fun writeToTempFile(bytes: ByteArray, fileName: String?): File {
        val extension = fileName?.substringAfterLast('.', "")
        val baseName = fileName?.substringBeforeLast('.') ?: "shadowtext_output"
        val suffix = if (!extension.isNullOrEmpty()) ".$extension" else ""
        val tempFile = File.createTempFile("${baseName}_", suffix, context.cacheDir)

        FileOutputStream(tempFile).use { it.write(bytes) }
        return tempFile
    }

    fun extensionFromMimeType(mimeType: String?): String {
        if (mimeType != null) {
            val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            if (ext != null) return ext
        }
        return "bin"
    }
}

class FileRepositoryException(message: String) : Exception(message)
