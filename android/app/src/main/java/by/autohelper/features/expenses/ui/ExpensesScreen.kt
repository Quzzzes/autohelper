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

data class ExpenseItem(val category: String, val amount: Double, val date: String, val note: String?)

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
fun ExpensesScreen() {
    val demoExpenses = listOf(
        ExpenseItem("fuel",       85.50,  "2025-05-08", "АЗС Лукойл"),
        ExpenseItem("repair",     320.00, "2025-05-01", "Замена тормозных колодок"),
        ExpenseItem("insurance",  210.00, "2025-04-15", "ОСГО продление"),
        ExpenseItem("wash",       25.00,  "2025-05-07", null),
    )

    val totalThisMonth = demoExpenses.sumOf { it.amount }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Расходы", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        LazyColumn(Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Расходы за май", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.bodyMedium)
                        Text("${totalThisMonth} BYN", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
            items(demoExpenses) { expense -> ExpenseCard(expense) }
        }
    }
}

@Composable
private fun ExpenseCard(expense: ExpenseItem) {
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
                if (expense.note != null) Text(expense.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(expense.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("${expense.amount} BYN", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}
