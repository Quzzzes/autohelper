package by.autohelper.features.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state         by viewModel.state.collectAsState()
    var showLogoutDlg by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Профиль", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            // Аватар
            Surface(
                modifier = Modifier.size(88.dp).clip(CircleShape),
                color    = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(state.email, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(Modifier.height(32.dp))

            // Карточки статистики
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Авто", state.carsCount.toString(), Icons.Default.DirectionsCar, Modifier.weight(1f))
                StatCard("Расходы", "${state.totalExpenses} BYN", Icons.Default.AccountBalanceWallet, Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            // Меню
            MenuRow(icon = Icons.Default.Notifications,     title = "Уведомления",    subtitle = "Напоминания о ТО и страховке")
            MenuRow(icon = Icons.Default.Info,              title = "О приложении",   subtitle = "Авто-помощник BY v1.0")

            Spacer(Modifier.weight(1f))

            // Выйти
            OutlinedButton(
                onClick  = { showLogoutDlg = true },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Icon(Icons.Default.Logout, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Выйти из аккаунта")
            }
        }
    }

    if (showLogoutDlg) {
        AlertDialog(
            onDismissRequest = { showLogoutDlg = false },
            title   = { Text("Выйти?") },
            text    = { Text("Вы выйдете из аккаунта ${state.email}.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.logout(); onLogout() },
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) { Text("Выйти") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDlg = false }) { Text("Отмена") } }
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, title: String, subtitle: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    HorizontalDivider(thickness = 0.5.dp)
}
