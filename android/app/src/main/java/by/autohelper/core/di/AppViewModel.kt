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

    // null = ещё определяем (сплэш), false = не залогинен, true = залогинен
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoggedIn.value = tokenStorage.isLoggedIn()
        }
    }

    fun onLoggedIn()  { _isLoggedIn.value = true  }
    fun onLoggedOut() { _isLoggedIn.value = false }
}
