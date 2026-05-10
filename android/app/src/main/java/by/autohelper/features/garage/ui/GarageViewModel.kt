package by.autohelper.features.garage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.network.AddCarRequest
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GarageState(
    val cars:      List<Car> = emptyList(),
    val isLoading: Boolean   = false,
    val error:     String?   = null,
)

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val api: ApiService,
) : ViewModel() {

    private val _state = MutableStateFlow(GarageState())
    val state = _state.asStateFlow()

    init { loadCars() }

    fun loadCars() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val response = api.getCars()
                if (response.success) {
                    _state.value = _state.value.copy(cars = response.data ?: emptyList())
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка загрузки")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun addCar(plate: String, make: String, model: String, year: Int) {
        viewModelScope.launch {
            try {
                val response = api.addCar(AddCarRequest(plate, make, model, year))
                if (response.success) loadCars()
                else _state.value = _state.value.copy(error = response.error?.message)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка добавления")
            }
        }
    }
}
