package by.autohelper.features.expenses.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import by.autohelper.core.network.Expense
import java.time.LocalDate

val CATEGORY_ICONS: Map<String, ImageVector> = mapOf(
    "fuel"       to Icons.Default.LocalGasStation,
    "repair"     to Icons.Default.Build,
    "insurance"  to Icons.Default.Security,
    "wash"       to Icons.Default.WaterDrop,
    "parking"    to Icons.Default.LocalParking,
    "fine"       to Icons.Default.Receipt,
    "techosmotr" to Icons.Default.VerifiedUser,
    "other"      to Icons.Default.MoreHoriz,
)

val CATEGORY_LABELS = mapOf(
    "fuel"       to "Топливо",
    "repair"     to "Ремонт / СТО",
    "insurance"  to "Страховка",
    "wash"       to "Мойка",
    "parking"    to "Парковка",
    "fine"       to "Штраф",
    "techosmotr" to "Техосмотр",
    "other"      to "Прочее",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(viewModel: ExpensesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Расходы", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            if (state.carId != null) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить расход")
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading    -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.carId == null -> NoCarSelected()
                else -> {
                    val total = state.expenses.sumOf { it.amount }
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            ) {
                                Column(Modifier.padding(20.dp)) {
                                    Text("Всего расходов", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                                    Text("%.2f BYN".format(total), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (state.expenses.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                                    Text("Расходов пока нет", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(state.expenses) { expense -> ExpenseCard(expense) }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { category, amount, liters, note ->
                viewModel.addExpense(category, amount, liters, note, LocalDate.now().toString())
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
private fun ExpenseCard(expense: Expense) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = CATEGORY_ICONS[expense.category] ?: Icons.Default.MoreHoriz,
                contentDescription = null,
                modifier    = Modifier.size(36.dp),
                tint        = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(CATEGORY_LABELS[expense.category] ?: expense.category, fontWeight = FontWeight.Bold)
                if (!expense.note.isNullOrBlank())
                    Text(expense.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (expense.liters != null && expense.liters > 0)
                    Text("${expense.liters} л", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(expense.expense_date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("%.2f BYN".format(expense.amount), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(onDismiss: () -> Unit, onConfirm: (String, Double, Double?, String?) -> Unit) {
    val categories = CATEGORY_LABELS.keys.toList()
    var selectedCategory by remember { mutableStateOf("fuel") }
    var amount  by remember { mutableStateOf("") }
    var liters  by remember { mutableStateOf("") }
    var note    by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить расход") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value         = CATEGORY_LABELS[selectedCategory] ?: selectedCategory,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Категория") },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier      = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text    = { Text(CATEGORY_LABELS[cat] ?: cat) },
                                onClick = { selectedCategory = cat; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(amount, { amount = it }, label = { Text("Сумма (BYN)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                if (selectedCategory == "fuel")
                    OutlinedTextField(liters, { liters = it }, label = { Text("Литры") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(note, { note = it }, label = { Text("Заметка (необязательно)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val a = amount.toDoubleOrNull() ?: return@Button
                    onConfirm(selectedCategory, a, liters.toDoubleOrNull(), note.takeIf { it.isNotBlank() })
                },
                enabled = amount.toDoubleOrNull() != null,
            ) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
