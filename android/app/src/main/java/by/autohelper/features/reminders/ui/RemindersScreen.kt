package by.autohelper.features.reminders.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import by.autohelper.core.network.Reminder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
fun RemindersScreen(viewModel: RemindersViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Напоминания", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            if (state.carId != null) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить напоминание")
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading    -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.carId == null -> NoCarSelected()
                state.reminders.isEmpty() -> EmptyRemindersState(onAdd = { showAddDialog = true })
                else -> RemindersList(state.reminders, onDelete = { viewModel.deleteReminder(it) })
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { type, date ->
                viewModel.addReminder(type, date)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun NoCarSelected() {
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.DirectionsCar, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text("Авто не выбрано", style = MaterialTheme.typography.titleLarge)
        Text("Выберите автомобиль в разделе «Гараж»", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyRemindersState(onAdd: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Notifications, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text("Напоминаний нет", style = MaterialTheme.typography.titleLarge)
        Text("Добавьте напоминание о ТО, страховке и т.д.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onAdd) { Text("Добавить") }
    }
}

@Composable
private fun RemindersList(reminders: List<Reminder>, onDelete: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(reminders) { reminder -> ReminderCard(reminder, onDelete = { onDelete(reminder.id) }) }
    }
}

@Composable
private fun ReminderCard(reminder: Reminder, onDelete: () -> Unit) {
    val today    = LocalDate.now()
    val dueDate  = runCatching { LocalDate.parse(reminder.due_date) }.getOrNull()
    val daysLeft = dueDate?.let { ChronoUnit.DAYS.between(today, it) }

    val chipColor = when {
        daysLeft == null        -> MaterialTheme.colorScheme.surfaceVariant
        daysLeft < 0            -> MaterialTheme.colorScheme.errorContainer
        daysLeft <= 14          -> MaterialTheme.colorScheme.tertiaryContainer
        else                    -> MaterialTheme.colorScheme.secondaryContainer
    }
    val chipText = when {
        daysLeft == null -> "?"
        daysLeft < 0     -> "Просрочено"
        daysLeft == 0L   -> "Сегодня!"
        else             -> "через $daysLeft дн."
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Notifications, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(REMINDER_LABELS[reminder.type] ?: reminder.type, fontWeight = FontWeight.Bold)
                Text("до ${reminder.due_date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Surface(color = chipColor, shape = MaterialTheme.shapes.small) {
                Text(chipText, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    val types = REMINDER_LABELS.keys.toList()
    var selectedType by remember { mutableStateOf("techosmotr") }
    var dateText     by remember { mutableStateOf(LocalDate.now().plusMonths(1).toString()) }
    var expanded     by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить напоминание") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value         = REMINDER_LABELS[selectedType] ?: selectedType,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Тип") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text    = { Text(REMINDER_LABELS[type] ?: type) },
                                onClick = { selectedType = type; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value         = dateText,
                    onValueChange = { dateText = it },
                    label         = { Text("Дата (ГГГГ-ММ-ДД)") },
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    placeholder   = { Text("2025-12-31") },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedType, dateText) },
                enabled = dateText.matches(Regex("\\d{4}-\\d{2}-\\d{2}")),
            ) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
