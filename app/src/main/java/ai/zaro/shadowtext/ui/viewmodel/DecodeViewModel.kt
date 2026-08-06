package ai.zaro.shadowtext.ui.viewmodel

import ai.zaro.shadowtext.core.engine.DecodeResult
import ai.zaro.shadowtext.core.engine.DetectionResult
import ai.zaro.shadowtext.domain.usecase.DecodeTextUseCase
import ai.zaro.shadowtext.domain.usecase.SaveAndShareUseCase
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

data class DecodeUiState(
    val isLoading: Boolean = false,
    val inputText: String = "",
    val detection: DetectionResult? = null,
    val result: DecodeResult? = null,
    val error: String? = null,
)

@HiltViewModel
class DecodeViewModel @Inject constructor(
    private val decodeTextUseCase: DecodeTextUseCase,
    private val saveAndShareUseCase: SaveAndShareUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DecodeUiState())
    val state: StateFlow<DecodeUiState> = _state.asStateFlow()

    fun setInputText(text: String) {
        _state.value = _state.value.copy(inputText = text, error = null)

        if (text.isNotBlank()) {
            detect(text)
        } else {
            _state.value = _state.value.copy(detection = null)
        }
    }

    private fun detect(text: String) {
        viewModelScope.launch {
            try {
                val detection = withContext(Dispatchers.Default) {
                    decodeTextUseCase.detect(text)
                }
                _state.value = _state.value.copy(detection = detection)
            } catch (_: Exception) {
            }
        }
    }

    fun decode() {
        val text = _state.value.inputText.ifBlank {
            _state.value = _state.value.copy(error = "No text to decode")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val result = withContext(Dispatchers.Default) {
                    decodeTextUseCase(text)
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    result = result,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Decoding failed: ${e.message}",
                )
            }
        }
    }

    fun getShareIntent(): android.content.Intent? {
        return null
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun reset() {
        _state.value = DecodeUiState()
    }
}
