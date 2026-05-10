package by.autohelper.features.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.autohelper.core.network.ApiService
import by.autohelper.core.network.AuthRequest
import by.autohelper.core.network.TokenStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val email:     String  = "",
    val password:  String  = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error:     String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: ApiService,
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onEmailChange(value: String)    { _state.value = _state.value.copy(email = value, error = null) }
    fun onPasswordChange(value: String) { _state.value = _state.value.copy(password = value, error = null) }

    fun login() = sendAuthRequest(isRegister = false)
    fun register() = sendAuthRequest(isRegister = true)

    private fun sendAuthRequest(isRegister: Boolean) {
        val s = _state.value
        if (s.email.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "Заполните все поля")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val request  = AuthRequest(s.email.trim(), s.password)
                val response = if (isRegister) api.register(request) else api.login(request)

                if (response.success && response.data != null) {
                    tokenStorage.saveTokens(response.data.accessToken, response.data.refreshToken)
                    _state.value = _state.value.copy(isLoggedIn = true)
                } else {
                    _state.value = _state.value.copy(error = response.error?.message ?: "Ошибка")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Нет соединения с сервером")
            } finally {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
