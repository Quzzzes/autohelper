package by.autohelper.features.fines.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.Fine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FinesState(
    val fines:     List<Fine> = emptyList(),
    val isLoading: Boolean    = false,
    val error:     String?    = null,
)

@HiltViewModel
class FinesViewModel @Inject constructor(private val api: ApiService) : ViewModel() {

    private val _state = MutableStateFlow(FinesState())
    val state = _state.asStateFlow()

    // Штрафы загружаются после выбора авто — пока показываем пустой список
    // TODO: подписаться на выбранное авто из GarageViewModel через SharedFlow
    init { _state.value = _state.value.copy(isLoading = false) }
}
