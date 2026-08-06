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

    suspend fun shareStegoText(content: String, fileName: String? = null): Intent {
        return saveAndShareUseCase.saveStegoText(content, fileName)
    }

    suspend fun shareDecodedFile(bytes: ByteArray, fileName: String?, mimeType: String?): Intent {
        return saveAndShareUseCase(bytes, fileName, mimeType)
    }
}
