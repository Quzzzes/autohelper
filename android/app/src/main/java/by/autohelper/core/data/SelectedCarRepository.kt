package by.autohelper.core.data

import by.autohelper.core.network.Car
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectedCarRepository @Inject constructor() {
    private val _selectedCar = MutableStateFlow<Car?>(null)
    val selectedCar = _selectedCar.asStateFlow()

    fun select(car: Car) { _selectedCar.value = car }
    fun clear()          { _selectedCar.value = null }
}
