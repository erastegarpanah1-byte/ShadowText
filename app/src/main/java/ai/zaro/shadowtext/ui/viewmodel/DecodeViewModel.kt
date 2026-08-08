package ai.zaro.shadowtext.ui.viewmodel

import ai.zaro.shadowtext.core.engine.DecodeResult
import ai.zaro.shadowtext.core.engine.DetectionResult
import ai.zaro.shadowtext.data.repository.FileRepository
import ai.zaro.shadowtext.domain.usecase.DecodeTextUseCase
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

enum class DecodeInputMode { TEXT, FILE }

data class DecodeUiState(
    val isLoading: Boolean = false,
    val inputMode: DecodeInputMode = DecodeInputMode.TEXT,
    val inputText: String = "",
    val selectedFileName: String? = null,
    val detection: DetectionResult? = null,
    val result: DecodeResult? = null,
    val error: String? = null,
)

@HiltViewModel
class DecodeViewModel @Inject constructor(
    private val decodeTextUseCase: DecodeTextUseCase,
    private val fileRepository: FileRepository,
) : ViewModel() {

    companion object { private const val TAG = "ShadowText:Decode" }

    private val _state = MutableStateFlow(DecodeUiState())
    val state: StateFlow<DecodeUiState> = _state.asStateFlow()
    private var fileBytes: ByteArray? = null

    fun setInputMode(mode: DecodeInputMode) { Log.d(TAG, "setInputMode: $mode"); _state.value = _state.value.copy(inputMode = mode, error = null) }

    fun setInputText(text: String) {
        _state.value = _state.value.copy(inputText = text, error = null)
        if (text.isNotBlank()) detect(text) else _state.value = _state.value.copy(detection = null)
    }

    fun loadFileForDecode(uri: Uri) {
        Log.d(TAG, "loadFileForDecode: $uri")
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val (bytes, meta) = withContext(Dispatchers.IO) { fileRepository.readUri(uri) }
                fileBytes = bytes; val text = String(bytes, Charsets.UTF_8)
                Log.d(TAG, "loadFileForDecode success: name=${meta.first}, textLength=${text.length}")
                _state.value = _state.value.copy(isLoading = false, selectedFileName = meta.first, inputText = text)
                detect(text)
            } catch (e: Exception) {
                Log.e(TAG, "loadFileForDecode failed", e)
                _state.value = _state.value.copy(isLoading = false, error = "Failed to read file: ${e.message}")
            }
        }
    }

    private fun detect(text: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.Default) { decodeTextUseCase.detect(text) }
                Log.d(TAG, "detect: hasPayload=${result.hasHiddenPayload}, scheme=${result.encodingScheme}")
                _state.value = _state.value.copy(detection = result)
            } catch (e: Exception) {
                Log.w(TAG, "detect failed (non-fatal)", e)
            }
        }
    }

    fun decode() {
        Log.d(TAG, "decode: inputMode=${_state.value.inputMode}")
        val text = when (_state.value.inputMode) {
            DecodeInputMode.TEXT -> _state.value.inputText.ifBlank { _state.value = _state.value.copy(error = "No text to decode"); return }
            DecodeInputMode.FILE -> if (fileBytes != null) String(fileBytes!!, Charsets.UTF_8) else { _state.value = _state.value.copy(error = "No file loaded"); return }
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result = withContext(Dispatchers.Default) { decodeTextUseCase(text) }
                Log.d(TAG, "decode success: payloadSize=${result.payload.size}, type=${result.payloadTypeLabel}")
                _state.value = _state.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                Log.e(TAG, "decode failed", e)
                _state.value = _state.value.copy(isLoading = false, error = "Decoding failed: ${e.message}")
            }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
    fun reset() { fileBytes = null; _state.value = DecodeUiState() }
}
