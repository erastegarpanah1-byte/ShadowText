package ai.zaro.shadowtext.domain.usecase

import ai.zaro.shadowtext.data.repository.FileRepository
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveAndShareUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileRepository: FileRepository,
) {
    suspend operator fun invoke(
        bytes: ByteArray,
        fileName: String?,
        mimeType: String?,
    ): Intent {
        val file = fileRepository.writeToTempFile(bytes, fileName)
        return createShareIntent(file, mimeType)
    }

    suspend fun saveStegoText(content: String, fileName: String?): Intent {
        val baseName = fileName?.substringBeforeLast('.') ?: "shadowtext_encoded"
        val file = File.createTempFile("${baseName}_", ".txt", context.cacheDir)
        file.writeText(content)
        return createShareIntent(file, "text/plain")
    }

    private fun createShareIntent(file: File, mimeType: String?): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = mimeType ?: "application/octet-stream"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
