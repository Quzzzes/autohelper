package by.autohelper.features.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.data.SelectedCarRepository
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val email:         String = "",
    val carsCount:     Int    = 0,
    val totalExpenses: String = "0",
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenStorage:    TokenStorage,
    private val api:             ApiService,
    private val selectedCarRepo: SelectedCarRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init { loadProfile() }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val carsResp = api.getCars()
                val cars     = carsResp.data ?: emptyList()

                // Считаем общие расходы по всем авто
                var total = 0.0
                cars.forEach { car ->
                    runCatching {
                        val exp = api.getExpenses(car.id)
                        total += exp.data?.sumOf { it.amount } ?: 0.0
                    }
                }

                _state.value = _state.value.copy(
                    carsCount     = cars.size,
                    totalExpenses = "%.0f".format(total),
                )
            } catch (_: Exception) {}
        }
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { api.logout() }
            tokenStorage.clearTokens()
            selectedCarRepo.clear()
        }
    }
}
