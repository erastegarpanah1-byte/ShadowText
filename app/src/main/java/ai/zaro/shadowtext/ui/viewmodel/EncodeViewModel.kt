package ai.zaro.shadowtext.ui.viewmodel

import ai.zaro.shadowtext.core.engine.EncodeResult
import ai.zaro.shadowtext.data.repository.FileRepository
import ai.zaro.shadowtext.domain.usecase.EncodeFileUseCase
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class EncodeUiState(
    val isLoading: Boolean = false,
    val selectedFileName: String? = null,
    val selectedFileSize: Long = 0,
    val selectedFileMimeType: String? = null,
    val result: EncodeResult? = null,
    val error: String? = null,
    val bytesLoaded: Boolean = false,
)

@HiltViewModel
class EncodeViewModel @Inject constructor(
    private val encodeFileUseCase: EncodeFileUseCase,
    private val fileRepository: FileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EncodeUiState())
    val state: StateFlow<EncodeUiState> = _state.asStateFlow()

    private var fileBytes: ByteArray? = null
    private var fileName: String? = null
    private var mimeType: String? = null

    fun loadFile(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val (bytes, meta) = withContext(Dispatchers.IO) {
                    fileRepository.readUri(uri)
                }

                fileBytes = bytes
                fileName = meta.first
                mimeType = meta.second

                _state.value = _state.value.copy(
                    isLoading = false,
                    selectedFileName = fileName,
                    selectedFileSize = bytes.size.toLong(),
                    selectedFileMimeType = mimeType,
                    bytesLoaded = true,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to read file: ${e.message}",
                )
            }
        }
    }

    fun encode() {
        val bytes = fileBytes ?: run {
            _state.value = _state.value.copy(error = "No file selected")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val result = withContext(Dispatchers.Default) {
                    encodeFileUseCase(bytes, mimeType, fileName)
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    result = result,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Encoding failed: ${e.message}",
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun reset() {
        fileBytes = null
        fileName = null
        mimeType = null
        _state.value = EncodeUiState()
    }
}
