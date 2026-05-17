package by.autohelper.features.expenses.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.data.SelectedCarRepository
import by.autohelper.core.network.AddExpenseRequest
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class ExpensesState(
    val expenses:  List<Expense> = emptyList(),
    val isLoading: Boolean       = false,
    val error:     String?       = null,
    val carId:     String?       = null,
)

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val api: ApiService,
    private val selectedCar: SelectedCarRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ExpensesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            selectedCar.selectedCar.collectLatest { car ->
                _state.value = _state.value.copy(carId = car?.id)
                if (car != null) load(car.id) else _state.value = _state.value.copy(expenses = emptyList())
            }
        }
    }

    fun load(carId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val r = api.getExpenses(carId)
                if (r.success) _state.value = _state.value.copy(expenses = r.data ?: emptyList())
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка загрузки")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun addExpense(category: String, amount: Double, liters: Double?, note: String?, date: String) {
        val carId = _state.value.carId ?: return
        viewModelScope.launch {
            try {
                val r = api.addExpense(AddExpenseRequest(carId, category, amount, liters, note.takeIf { !it.isNullOrBlank() }, date))
                if (r.success) load(carId)
                else _state.value = _state.value.copy(error = r.error?.message)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Ошибка добавления")
            }
        }
    }
}
