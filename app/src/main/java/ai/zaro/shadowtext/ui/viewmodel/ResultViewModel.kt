package ai.zaro.shadowtext.ui.viewmodel

import ai.zaro.shadowtext.domain.usecase.SaveAndShareUseCase
import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val saveAndShareUseCase: SaveAndShareUseCase,
) : ViewModel() {
    suspend fun shareStegoText(content: String, fileName: String? = null): Intent =
        saveAndShareUseCase.saveStegoText(content, fileName)
    suspend fun shareDecodedText(bytes: ByteArray, fileName: String?, mimeType: String?): Intent =
        saveAndShareUseCase(bytes, fileName, mimeType)
}
