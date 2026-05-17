package by.autohelper.features.garage.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.data.SelectedCarRepository
import by.autohelper.core.network.AddCarRequest
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GarageState(
    val cars:        List<Car> = emptyList(),
    val selectedCar: Car?      = null,
    val isLoading:   Boolean   = false,
    val error:       String?   = null,
)

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val api: ApiService,
    private val selectedCarRepo: SelectedCarRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(GarageState())
    val state = _state.asStateFlow()

    init { loadCars() }

    fun loadCars() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = api.getCars()
                if (response.success) {
                    val cars = response.data ?: emptyList()
                    val selected = _state.value.selectedCar?.let { prev ->
                        cars.find { it.id == prev.id } ?: cars.firstOrNull()
                    } ?: cars.firstOrNull()
                    _state.value = _state.value.copy(cars = cars, selectedCar = selected)
                    selected?.let { selectedCarRepo.select(it) }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка загрузки")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun selectCar(car: Car) {
        _state.value = _state.value.copy(selectedCar = car)
        selectedCarRepo.select(car)
    }

    fun addCar(plate: String, make: String, model: String, year: Int) {
        viewModelScope.launch {
            try {
                val r = api.addCar(AddCarRequest(plate, make, model, year))
                if (r.success) loadCars()
                else _state.value = _state.value.copy(error = r.error?.message)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка добавления")
            }
        }
    }

    fun deleteCar(carId: String) {
        viewModelScope.launch {
            try {
                api.deleteCar(carId)
                if (_state.value.selectedCar?.id == carId) selectedCarRepo.clear()
                loadCars()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка удаления")
            }
        }
    }
}
