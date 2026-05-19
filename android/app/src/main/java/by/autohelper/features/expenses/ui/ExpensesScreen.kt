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
    val state   by viewModel.state.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Расходы", fontWeight = FontWeight.Bold) }) },
        floatingActionButton = {
            if (state.carId != null) {
                FloatingActionButton(onClick = { showAdd = true }) {
                    Icon(Icons.Default.Add, "Добавить расход")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        when {
            state.carId == null -> NoCarSelectedState()
            state.isLoading     -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
            else -> {
                val total = state.expenses.sumOf { it.amount }
                LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Text("Расходы всего", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "%.2f BYN".format(total),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    if (state.expenses.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(top = 48.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.AccountBalanceWallet, null, Modifier.size(64.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                                    Spacer(Modifier.height(12.dp))
                                    Text("Расходов нет", style = MaterialTheme.typography.titleMedium)
                                    Text("Нажмите + чтобы добавить", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        items(state.expenses, key = { it.id }) { expense -> ExpenseCard(expense) }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddExpenseDialog(
            onDismiss = { showAdd = false },
            onConfirm = { category, amount, liters, note ->
                viewModel.addExpense(category, amount, liters, note, LocalDate.now().toString())
                showAdd = false
            }
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
                if (!expense.note.isNullOrBlank()) Text(expense.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (expense.liters != null) Text("${expense.liters} л", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    var selectedCat  by remember { mutableStateOf(categories.first()) }
    var expanded     by remember { mutableStateOf(false) }
    var amount       by remember { mutableStateOf("") }
    var liters       by remember { mutableStateOf("") }
    var note         by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить расход") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value          = CATEGORY_LABELS[selectedCat] ?: selectedCat,
                        onValueChange  = {},
                        readOnly       = true,
                        label          = { Text("Категория") },
                        trailingIcon   = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier       = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text    = { Text(CATEGORY_LABELS[cat] ?: cat) },
                                onClick = { selectedCat = cat; expanded = false }
                            )
                        }
                    }
                }
                OutlinedTextField(amount, { amount = it }, label = { Text("Сумма (BYN)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                if (selectedCat == "fuel") {
                    OutlinedTextField(liters, { liters = it }, label = { Text("Литры (необязательно)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
                OutlinedTextField(note, { note = it }, label = { Text("Заметка (необязательно)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedCat, amount.toDoubleOrNull() ?: 0.0, liters.toDoubleOrNull(), note.takeIf { it.isNotBlank() })
                },
                enabled = amount.toDoubleOrNull() != null && amount.toDoubleOrNull()!! > 0,
            ) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
