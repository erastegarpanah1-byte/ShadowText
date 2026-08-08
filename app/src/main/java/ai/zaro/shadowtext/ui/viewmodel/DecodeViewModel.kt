package ai.zaro.shadowtext.ui.viewmodel

import ai.zaro.shadowtext.core.engine.DecodeResult
import ai.zaro.shadowtext.domain.usecase.DecodeTextUseCase
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
    val result: DecodeResult? = null,
    val decodedText: String? = null,
    val error: String? = null,
)

@HiltViewModel
class DecodeViewModel @Inject constructor(
    private val decodeTextUseCase: DecodeTextUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DecodeUiState())
    val state: StateFlow<DecodeUiState> = _state.asStateFlow()

    fun decode(text: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.Default) { decodeTextUseCase(text) }
                val decoded = String(result.payload, Charsets.UTF_8)
                _state.value = _state.value.copy(isLoading = false, result = result, decodedText = decoded)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Decoding failed: ${"$"}{e.message}")
            }
        }
    }
    fun reset() { _state.value = DecodeUiState() }
}
