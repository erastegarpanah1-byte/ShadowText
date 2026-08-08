package ai.zaro.shadowtext.domain.usecase

import ai.zaro.shadowtext.core.engine.EncodeResult
import ai.zaro.shadowtext.core.engine.StegoEncoder
import javax.inject.Inject

class EncodeFileUseCase @Inject constructor(private val stegoEncoder: StegoEncoder) {
    fun invoke(payload: ByteArray, mimeType: String? = null, fileName: String? = null, carrierText: String): EncodeResult {
        return stegoEncoder.encode(payload = payload, mimeType = mimeType, fileName = fileName, carrierText = carrierText)
    }
}
