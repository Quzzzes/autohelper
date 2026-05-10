package by.autohelper.features.sto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import by.autohelper.core.network.Sto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoScreen(viewModel: StoViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Рейтинг СТО", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading   -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.list.isEmpty() -> Text("Нет данных", Modifier.align(Alignment.Center))
                else -> StoList(state.list)
            }
        }
    }
}

@Composable
private fun StoList(list: List<Sto>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(list) { sto -> StoCard(sto) }
    }
}

@Composable
private fun StoCard(sto: Sto) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(if (sto.is_premium) 4.dp else 2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Build, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(sto.name, fontWeight = FontWeight.Bold)
                        if (sto.is_premium) Text("Premium", color = Color(0xFFFFA000), style = MaterialTheme.typography.labelSmall)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, Modifier.size(16.dp), tint = Color(0xFFFFA000))
                    Text(" ${sto.rating}", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(sto.address ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(sto.phone   ?: "", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text("${sto.reviews_count} отзывов", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
