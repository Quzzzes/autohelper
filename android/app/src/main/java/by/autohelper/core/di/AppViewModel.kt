package by.autohelper.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.network.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    // null = сплэш, false = логин, true = главный экран
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        // Подписываемся на Flow — DataStore сам уведомит когда данные будут готовы
        viewModelScope.launch {
            tokenStorage.isLoggedInFlow.collect { loggedIn ->
                // Обновляем только если ещё на сплэше, или меняем состояние
                if (_isLoggedIn.value == null || _isLoggedIn.value == true) {
                    _isLoggedIn.value = loggedIn
                }
            }
        }
    }

    fun onLoggedIn()  { _isLoggedIn.value = true  }
    fun onLoggedOut() { _isLoggedIn.value = false }
}
