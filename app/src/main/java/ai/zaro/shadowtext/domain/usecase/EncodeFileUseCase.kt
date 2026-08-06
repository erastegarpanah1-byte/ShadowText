package ai.zaro.shadowtext.domain.usecase

import ai.zaro.shadowtext.core.engine.EncodeResult
import ai.zaro.shadowtext.core.engine.StegoEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncodeFileUseCase @Inject constructor(
    private val encoder: StegoEncoder,
) {
    suspend operator fun invoke(
        bytes: ByteArray,
        mimeType: String? = null,
        fileName: String? = null,
    ): EncodeResult {
        return encoder.encode(
            payload = bytes,
            mimeType = mimeType,
            fileName = fileName,
        )
    }
}
