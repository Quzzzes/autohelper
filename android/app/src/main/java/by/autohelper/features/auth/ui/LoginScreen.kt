package by.autohelper.features.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    Column(
        modifier            = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.DirectionsCar,
            contentDescription = null,
            modifier    = Modifier.size(72.dp),
            tint        = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text       = "Авто-помощник BY",
            fontSize   = 24.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.primary,
        )

        Text(
            text     = "Штрафы · ТО · СТО · Расходы",
            fontSize = 14.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(48.dp))

        OutlinedTextField(
            value         = state.email,
            onValueChange = viewModel::onEmailChange,
            label         = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier      = Modifier.fillMaxWidth(),
            singleLine    = true,
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value               = state.password,
            onValueChange       = viewModel::onPasswordChange,
            label               = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions     = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier            = Modifier.fillMaxWidth(),
            singleLine          = true,
        )

        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick  = viewModel::login,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled  = !state.isLoading,
        ) {
            if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            else Text("Войти", fontSize = 16.sp)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick  = viewModel::register,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled  = !state.isLoading,
        ) {
            Text("Зарегистрироваться", fontSize = 16.sp)
        }
    }
}
