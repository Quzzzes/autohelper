package by.autohelper.features.fines.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Receipt
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
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Штрафы ГАИ", fontWeight = FontWeight.Bold)
                        if (state.carPlate != null)
                            Text(state.carPlate!!, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading       -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.carPlate == null -> NoCarSelected()
                state.fines.isEmpty() -> EmptyFinesState()
                else                  -> FinesList(state.fines)
            }
        }
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
private fun FinesList(fines: List<Fine>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(fines) { fine -> FineCard(fine) }
    }
}

@Composable
private fun FineCard(fine: Fine) {
    val isPaid       = fine.status == "paid"
    val isOverdue    = fine.status == "overdue"
    val statusColor  = when {
        isPaid    -> Color(0xFF388E3C)
        isOverdue -> Color(0xFFFF6F00)
        else      -> Color(0xFFD32F2F)
    }
    val statusText = when {
        isPaid    -> "Оплачен"
        isOverdue -> "Просрочен"
        else      -> "Не оплачен"
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("№ ${fine.resolution_no ?: "—"}", fontWeight = FontWeight.Bold)
                Text(statusText, color = statusColor, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
            Text("Статья: ${fine.article ?: "—"}", style = MaterialTheme.typography.bodyMedium)
            Text("Дата: ${fine.fine_date ?: "—"}",  style = MaterialTheme.typography.bodySmall)
            if (fine.erip_invoice_no != null)
                Text("ЕРИП: ${fine.erip_invoice_no}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${fine.amount} BYN", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                if (!isPaid) {
                    Button(
                        onClick = {},
                        colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) { Text("Оплатить ЕРИП") }
                }
            }
        }
    }
}

@Composable
private fun EmptyFinesState() {
    Column(Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Receipt, null, Modifier.size(80.dp), tint = Color(0xFF388E3C))
        Spacer(Modifier.height(16.dp))
        Text("Штрафов нет", style = MaterialTheme.typography.titleLarge)
        Text("Отличное вождение!", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
