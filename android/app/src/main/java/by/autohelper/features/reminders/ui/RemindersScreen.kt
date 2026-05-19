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
import androidx.compose.ui.graphics.Color
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

val REMINDER_ICONS = mapOf(
    "osgo"       to Icons.Default.Security,
    "to"         to Icons.Default.Build,
    "techosmotr" to Icons.Default.VerifiedUser,
    "blue_card"  to Icons.Default.CreditCard,
    "oil"        to Icons.Default.WaterDrop,
    "tires"      to Icons.Default.DirectionsCar,
    "vu"         to Icons.Default.Badge,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(viewModel: RemindersViewModel = hiltViewModel()) {
    val state    by viewModel.state.collectAsState()
    var showAdd  by remember { mutableStateOf(false) }
    val snackbar  = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Напоминания", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            if (state.carId != null) {
                FloatingActionButton(onClick = { showAdd = true }) {
                    Icon(Icons.Default.Add, "Добавить")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        when {
            state.carId == null -> NoCarSelectedState()
            state.isLoading     -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
            state.reminders.isEmpty() -> EmptyRemindersState(onAdd = { showAdd = true })
            else -> LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.reminders, key = { it.id }) { reminder ->
                    ReminderCard(reminder = reminder, onDelete = { viewModel.deleteReminder(reminder.id) })
                }
            }
        }
    }

    if (showAdd) {
        AddReminderDialog(
            onDismiss = { showAdd = false },
            onConfirm = { type, date -> viewModel.addReminder(type, date); showAdd = false }
        )
    }
}

@Composable
private fun NoCarSelectedState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.DirectionsCar, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
            Text("Выберите автомобиль", style = MaterialTheme.typography.titleLarge)
            Text("Перейдите в Гараж и выберите авто", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyRemindersState(onAdd: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Notifications, null, Modifier.size(72.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
            Text("Напоминаний нет", style = MaterialTheme.typography.titleLarge)
            Text("Добавьте напоминание о ТО, страховке и др.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Button(onClick = onAdd) { Text("Добавить") }
        }
    }
}

@Composable
private fun ReminderCard(reminder: Reminder, onDelete: () -> Unit) {
    val today    = LocalDate.now()
    val dueDate  = runCatching { LocalDate.parse(reminder.due_date) }.getOrNull()
    val daysLeft = dueDate?.let { ChronoUnit.DAYS.between(today, it) }

    val urgencyColor = when {
        daysLeft == null       -> MaterialTheme.colorScheme.onSurfaceVariant
        daysLeft < 0           -> Color(0xFFD32F2F)   // просрочено
        daysLeft <= 14         -> Color(0xFFE65100)   // скоро
        daysLeft <= 30         -> Color(0xFFF9A825)   // в этом месяце
        else                   -> Color(0xFF388E3C)   // всё ок
    }

    val urgencyText = when {
        daysLeft == null -> ""
        daysLeft < 0    -> "Просрочено на ${-daysLeft} дн."
        daysLeft == 0L  -> "Сегодня!"
        daysLeft == 1L  -> "Завтра!"
        daysLeft <= 30  -> "Через $daysLeft дн."
        else            -> "Через $daysLeft дн."
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = REMINDER_ICONS[reminder.type] ?: Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = urgencyColor,
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(REMINDER_LABELS[reminder.type] ?: reminder.type, fontWeight = FontWeight.Bold)
                Text("до ${reminder.due_date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (urgencyText.isNotEmpty()) {
                    Text(urgencyText, style = MaterialTheme.typography.labelSmall, color = urgencyColor, fontWeight = FontWeight.Medium)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Удалить", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    val types    = REMINDER_LABELS.keys.toList()
    var selType  by remember { mutableStateOf(types.first()) }
    var expanded by remember { mutableStateOf(false) }

    // Дата — простой ввод YYYY-MM-DD
    val nextYear = LocalDate.now().plusMonths(11).format(DateTimeFormatter.ISO_LOCAL_DATE)
    var dateText by remember { mutableStateOf(nextYear) }
    var dateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить напоминание") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Тип
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value         = REMINDER_LABELS[selType] ?: selType,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Тип") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        types.forEach { t ->
                            DropdownMenuItem(text = { Text(REMINDER_LABELS[t] ?: t) }, onClick = { selType = t; expanded = false })
                        }
                    }
                }
                // Дата
                OutlinedTextField(
                    value         = dateText,
                    onValueChange = { dateText = it; dateError = false },
                    label         = { Text("Дата (ГГГГ-ММ-ДД)") },
                    isError       = dateError,
                    supportingText = if (dateError) {{ Text("Формат: 2025-12-31") }} else null,
                    modifier      = Modifier.fillMaxWidth(),
                    singleLine    = true,
                    leadingIcon   = { Icon(Icons.Default.CalendarMonth, null) },
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val parsed = runCatching { LocalDate.parse(dateText) }.getOrNull()
                if (parsed == null) { dateError = true; return@Button }
                onConfirm(selType, dateText)
            }) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
