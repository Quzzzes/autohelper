package by.autohelper.features.fines.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.data.SelectedCarRepository
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.Fine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FinesState(
    val fines:     List<Fine> = emptyList(),
    val isLoading: Boolean    = false,
    val error:     String?    = null,
    val carPlate:  String?    = null,
)

@HiltViewModel
class FinesViewModel @Inject constructor(
    private val api: ApiService,
    private val selectedCar: SelectedCarRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FinesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            selectedCar.selectedCar.collectLatest { car ->
                _state.value = _state.value.copy(carPlate = car?.plate)
                if (car != null) load(car.id) else _state.value = _state.value.copy(fines = emptyList())
            }
        }
    }

    fun load(carId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val r = api.getFines(carId)
                if (r.success) _state.value = _state.value.copy(fines = r.data ?: emptyList())
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка загрузки")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
