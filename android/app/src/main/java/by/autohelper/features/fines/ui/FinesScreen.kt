package by.autohelper.features.fines.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import by.autohelper.core.network.Fine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinesScreen(viewModel: FinesViewModel = hiltViewModel()) {
    val state    by viewModel.state.collectAsState()
    val snackbar  = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let { snackbar.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Штрафы ГАИ", fontWeight = FontWeight.Bold)
                        if (state.carPlate != null)
                            Text(state.carPlate!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    if (state.carPlate != null) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, "Обновить")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.carPlate == null -> NoCarSelectedState()
                state.isLoading        -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.fines.isEmpty()  -> EmptyFinesState(plate = state.carPlate!!)
                else                   -> FinesList(state.fines)
            }
        }
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
private fun EmptyFinesState(plate: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Receipt, null, Modifier.size(80.dp), tint = Color(0xFF388E3C))
            Text("Штрафов нет", style = MaterialTheme.typography.titleLarge)
            Text("Для номера $plate", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Отличное вождение! 🎉", color = Color(0xFF388E3C))
        }
    }
}

@Composable
private fun FinesList(fines: List<Fine>) {
    val unpaid = fines.count { it.status != "paid" }
    val total  = fines.filter { it.status != "paid" }.sumOf { it.amount }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (unpaid > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F)),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Неоплаченных штрафов: $unpaid", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Итого к оплате: %.2f BYN".format(total), color = Color.White, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        items(fines, key = { it.id }) { fine -> FineCard(fine) }
    }
}

@Composable
private fun FineCard(fine: Fine) {
    val isPaid      = fine.status == "paid"
    val statusColor = if (isPaid) Color(0xFF388E3C) else Color(0xFFD32F2F)
    val statusText  = when (fine.status) { "paid" -> "Оплачен"; "overdue" -> "Просрочен"; else -> "Не оплачен" }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("№ ${fine.resolution_no ?: "—"}", fontWeight = FontWeight.Bold)
                Text(statusText, color = statusColor, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(6.dp))
            if (!fine.article.isNullOrBlank())  Text("Статья: ${fine.article}", style = MaterialTheme.typography.bodyMedium)
            if (!fine.fine_date.isNullOrBlank()) Text("Дата: ${fine.fine_date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("%.2f BYN".format(fine.amount), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                if (!isPaid) {
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) { Text("Оплатить ЕРИП") }
                }
            }
        }
    }
}
