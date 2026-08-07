package ai.zaro.shadowtext.ui.viewmodel

import ai.zaro.shadowtext.core.engine.DecodeResult
import ai.zaro.shadowtext.core.engine.DetectionResult
import ai.zaro.shadowtext.data.repository.FileRepository
import ai.zaro.shadowtext.domain.usecase.DecodeTextUseCase
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

    private val _state = MutableStateFlow(DecodeUiState())
    val state: StateFlow<DecodeUiState> = _state.asStateFlow()
    private var fileBytes: ByteArray? = null

    fun setInputMode(mode: DecodeInputMode) { _state.value = _state.value.copy(inputMode = mode, error = null) }
    fun setInputText(text: String) { _state.value = _state.value.copy(inputText = text, error = null); if (text.isNotBlank()) detect(text) else _state.value = _state.value.copy(detection = null) }

    fun loadFileForDecode(uri: Uri) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val (bytes, meta) = withContext(Dispatchers.IO) { fileRepository.readUri(uri) }
                fileBytes = bytes; val text = String(bytes, Charsets.UTF_8)
                _state.value = _state.value.copy(isLoading = false, selectedFileName = meta.first, inputText = text)
                detect(text)
            } catch (e: Exception) { _state.value = _state.value.copy(isLoading = false, error = "Failed to read file: ${e.message}") }
        }
    }

    private fun detect(text: String) {
        viewModelScope.launch { try { _state.value = _state.value.copy(detection = withContext(Dispatchers.Default) { decodeTextUseCase.detect(text) }) } catch (_: Exception) {} }
    }

    fun decode() {
        val text = when (_state.value.inputMode) {
            DecodeInputMode.TEXT -> _state.value.inputText.ifBlank { _state.value = _state.value.copy(error = "No text to decode"); return }
            DecodeInputMode.FILE -> if (fileBytes != null) String(fileBytes!!, Charsets.UTF_8) else { _state.value = _state.value.copy(error = "No file loaded"); return }
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try { _state.value = _state.value.copy(isLoading = false, result = withContext(Dispatchers.Default) { decodeTextUseCase(text) }) }
            catch (e: Exception) { _state.value = _state.value.copy(isLoading = false, error = "Decoding failed: ${e.message}") }
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }
    fun reset() { fileBytes = null; _state.value = DecodeUiState() }
}
