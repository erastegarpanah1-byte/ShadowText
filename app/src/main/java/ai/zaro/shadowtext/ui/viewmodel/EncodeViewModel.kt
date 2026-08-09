package ai.zaro.shadowtext.ui.viewmodel

import ai.zaro.shadowtext.core.engine.EncodeResult
import ai.zaro.shadowtext.domain.usecase.EncodeFileUseCase
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
    val result: EncodeResult? = null,
    val stegoText: String? = null,
    val error: String? = null,
)

@HiltViewModel
class EncodeViewModel @Inject constructor(
    private val encodeFileUseCase: EncodeFileUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(EncodeUiState())
    val state: StateFlow<EncodeUiState> = _state.asStateFlow()

    fun encode(secretText: String, carrierText: String) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.Default) {
                    encodeFileUseCase(secretText.toByteArray(Charsets.UTF_8), "text/plain", "message.txt", carrierText = carrierText)
                }
                _state.value = _state.value.copy(isLoading = false, result = result, stegoText = result.stegoText)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Encoding failed: ${e.message}")
            }
        }
    }
    fun reset() { _state.value = EncodeUiState() }
}
