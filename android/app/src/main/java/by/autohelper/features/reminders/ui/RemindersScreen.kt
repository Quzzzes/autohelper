package by.autohelper.features.reminders.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val REMINDER_LABELS = mapOf(
    "osgo"       to "Страховка ОСГО",
    "to"         to "Техническое обслуживание",
    "techosmotr" to "Техосмотр",
    "blue_card"  to "Синяя карта",
    "oil"        to "Замена масла",
    "tires"      to "Смена резины",
    "vu"         to "Водительское удостоверение",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen() {
    // Демо-данные пока не выбрано авто
    val demoReminders = listOf(
        "techosmotr" to "2025-08-15",
        "osgo"       to "2025-11-01",
        "oil"        to "2025-06-20",
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Напоминания", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(demoReminders) { (type, date) ->
                ReminderCard(type = type, dueDate = date)
            }
        }
    }
}

@Composable
private fun ReminderCard(type: String, dueDate: String) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(REMINDER_LABELS[type] ?: type, fontWeight = FontWeight.Bold)
                Text("до $dueDate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
