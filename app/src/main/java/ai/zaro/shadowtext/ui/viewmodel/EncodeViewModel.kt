package ai.zaro.shadowtext.ui.viewmodel

import ai.zaro.shadowtext.core.engine.EncodeResult
import ai.zaro.shadowtext.data.repository.FileRepository
import ai.zaro.shadowtext.domain.usecase.EncodeFileUseCase
import android.net.Uri
import android.util.Log
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

enum class EncodeMode { FILE, TEXT }

data class EncodeUiState(
    val isLoading: Boolean = false,
    val mode: EncodeMode = EncodeMode.TEXT,
    val selectedFileName: String? = null,
    val selectedFileSize: Long = 0,
    val selectedFileMimeType: String? = null,
    val bytesLoaded: Boolean = false,
    val inputText: String = "",
    val useCarrierText: Boolean = true,
    val carrierText: String = "",
    val result: EncodeResult? = null,
    val error: String? = null,
)

@HiltViewModel
class EncodeViewModel @Inject constructor(
    private val encodeFileUseCase: EncodeFileUseCase,
    private val fileRepository: FileRepository,
) : ViewModel() {

    companion object { private const val TAG = "ShadowText:Encode" }

    private val _state = MutableStateFlow(EncodeUiState())
    val state: StateFlow<EncodeUiState> = _state.asStateFlow()

    private var fileBytes: ByteArray? = null
    private var fileName: String? = null
    private var mimeType: String? = null

    fun setMode(mode: EncodeMode) { Log.d(TAG, "setMode: $mode"); _state.value = _state.value.copy(mode = mode, error = null) }
    fun setInputText(text: String) { _state.value = _state.value.copy(inputText = text, error = null) }
    fun toggleCarrierText() { _state.value = _state.value.copy(useCarrierText = !_state.value.useCarrierText) }
    fun setCarrierText(text: String) { _state.value = _state.value.copy(carrierText = text) }

    fun loadFile(uri: Uri) {
        Log.d(TAG, "loadFile: $uri")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val (bytes, meta) = withContext(Dispatchers.IO) { fileRepository.readUri(uri) }
                fileBytes = bytes; fileName = meta.first; mimeType = meta.second
                Log.d(TAG, "loadFile success: name=$fileName, mime=$mimeType, size=${bytes.size}")
                _state.value = _state.value.copy(isLoading = false, selectedFileName = fileName, selectedFileSize = bytes.size.toLong(), selectedFileMimeType = mimeType, bytesLoaded = true)
            } catch (e: Exception) {
                Log.e(TAG, "loadFile failed", e)
                _state.value = _state.value.copy(isLoading = false, error = "Failed to read file: ${e.message}")
            }
        }
    }

    fun encode() {
        Log.d(TAG, "encode: mode=${_state.value.mode}")
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val result: EncodeResult = when (_state.value.mode) {
                    EncodeMode.FILE -> {
                        val bytes = fileBytes ?: run { _state.value = _state.value.copy(isLoading = false, error = "No file selected"); return@launch }
                        Log.d(TAG, "encoding file: ${bytes.size} bytes")
                        withContext(Dispatchers.Default) { encodeFileUseCase(bytes, mimeType, fileName) }
                    }
                    EncodeMode.TEXT -> {
                        val text = _state.value.inputText.ifBlank { _state.value = _state.value.copy(isLoading = false, error = "Enter text to hide"); return@launch }
                        val bytes = text.toByteArray(Charsets.UTF_8)
                        Log.d(TAG, "encoding text: ${bytes.size} bytes")
                        withContext(Dispatchers.Default) { encodeFileUseCase(bytes, "text/plain", "message.txt") }
                    }
                }
                Log.d(TAG, "encode success: stegoText length=${result.stegoText.length}")
                _state.value = _state.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                Log.e(TAG, "encode failed", e)
                _state.value = _state.value.copy(isLoading = false, error = "Encoding failed: ${e.message}")
            }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
    fun reset() { fileBytes = null; fileName = null; mimeType = null; _state.value = EncodeUiState() }
}
