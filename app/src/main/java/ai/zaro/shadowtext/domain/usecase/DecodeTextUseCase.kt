package ai.zaro.shadowtext.domain.usecase

import ai.zaro.shadowtext.core.engine.DecodeResult
import ai.zaro.shadowtext.core.engine.DetectionResult
import ai.zaro.shadowtext.core.engine.StegoDecoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DecodeTextUseCase @Inject constructor(
    private val decoder: StegoDecoder,
) {
    suspend operator fun invoke(text: String): DecodeResult {
        return decoder.decode(text)
    }

    suspend fun detect(text: String): DetectionResult {
        return decoder.detect(text)
    }
}
