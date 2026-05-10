package by.autohelper.features.sto.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.Sto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoState(val list: List<Sto> = emptyList(), val isLoading: Boolean = false)

@HiltViewModel
class StoViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _state = MutableStateFlow(StoState())
    val state = _state.asStateFlow()

    init { loadSto() }

    private fun loadSto() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val response = api.getStoList()
                if (response.success) _state.value = _state.value.copy(list = response.data ?: emptyList())
            } catch (e: Exception) {
                // Показываем пустой список при ошибке сети
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
