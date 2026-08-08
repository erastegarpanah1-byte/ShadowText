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

enum class EncodeMode { TEXT, FILE }

data class EncodeUiState(
    val isLoading: Boolean = false,
    val mode: EncodeMode = EncodeMode.TEXT,
    val secretText: String = "",
    val carrierText: String = "",
    val selectedFileName: String? = null,
    val selectedFileSize: Long = 0,
    val selectedFileMimeType: String? = null,
    val bytesLoaded: Boolean = false,
    val result: EncodeResult? = null,
    val error: String? = null,
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

    fun setMode(mode: EncodeMode) { _state.value = _state.value.copy(mode = mode, error = null) }
    fun setSecretText(text: String) { _state.value = _state.value.copy(secretText = text, error = null) }
    fun setCarrierText(text: String) { _state.value = _state.value.copy(carrierText = text, error = null) }

    fun loadFile(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val (bytes, meta) = withContext(Dispatchers.IO) { fileRepository.readUri(uri) }
                fileBytes = bytes; fileName = meta.first; mimeType = meta.second
                _state.value = _state.value.copy(isLoading = false, selectedFileName = fileName, selectedFileSize = bytes.size.toLong(), selectedFileMimeType = mimeType, bytesLoaded = true)
            } catch (e: Exception) { _state.value = _state.value.copy(isLoading = false, error = "Failed to read file: ${e.message}") }
        }
    }

    fun encode() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result: EncodeResult = when (_state.value.mode) {
                    EncodeMode.TEXT -> {
                        val secret = _state.value.secretText
                        val cover = _state.value.carrierText
                        if (secret.isBlank()) { _state.value = _state.value.copy(isLoading = false, error = "Please enter the secret message"); return@launch }
                        if (cover.isBlank()) { _state.value = _state.value.copy(isLoading = false, error = "Please enter the cover text"); return@launch }
                        withContext(Dispatchers.Default) { encodeFileUseCase(payload = secret.toByteArray(Charsets.UTF_8), mimeType = "text/plain", fileName = "message.txt", carrierText = cover) }
                    }
                    EncodeMode.FILE -> {
                        val bytes = fileBytes; val cover = _state.value.carrierText
                        if (bytes == null) { _state.value = _state.value.copy(isLoading = false, error = "No file selected"); return@launch }
                        if (cover.isBlank()) { _state.value = _state.value.copy(isLoading = false, error = "Please enter the cover text"); return@launch }
                        withContext(Dispatchers.Default) { encodeFileUseCase(payload = bytes, mimeType = mimeType, fileName = fileName, carrierText = cover) }
                    }
                }
                _state.value = _state.value.copy(isLoading = false, result = result)
            } catch (e: Exception) { _state.value = _state.value.copy(isLoading = false, error = "Encoding failed: ${e.message}") }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
    fun reset() { fileBytes = null; fileName = null; mimeType = null; _state.value = EncodeUiState() }
}
