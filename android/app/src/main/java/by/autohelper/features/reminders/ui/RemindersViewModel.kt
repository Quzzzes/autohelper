package by.autohelper.features.reminders.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.data.SelectedCarRepository
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.CreateReminderRequest
import by.autohelper.core.network.Reminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RemindersState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean        = false,
    val error:     String?        = null,
    val carId:     String?        = null,
)

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val api: ApiService,
    private val selectedCar: SelectedCarRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RemindersState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            selectedCar.selectedCar.collectLatest { car ->
                _state.value = _state.value.copy(carId = car?.id)
                if (car != null) load(car.id) else _state.value = _state.value.copy(reminders = emptyList())
            }
        }
    }

    fun load(carId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val r = api.getReminders(carId)
                if (r.success) _state.value = _state.value.copy(reminders = r.data ?: emptyList())
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка загрузки")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun addReminder(type: String, dueDate: String) {
        val carId = _state.value.carId ?: return
        viewModelScope.launch {
            try {
                val r = api.createReminder(CreateReminderRequest(carId, type, dueDate))
                if (r.success) load(carId)
                else _state.value = _state.value.copy(error = r.error?.message)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка добавления")
            }
        }
    }

    fun deleteReminder(id: String) {
        val carId = _state.value.carId ?: return
        viewModelScope.launch {
            try {
                api.deleteReminder(id)
                load(carId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка удаления")
            }
        }
    }
}
