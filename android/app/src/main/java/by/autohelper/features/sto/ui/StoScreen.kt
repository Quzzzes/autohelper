package by.autohelper.features.sto.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
    val state  by viewModel.state.collectAsState()
    var search by remember { mutableStateOf("") }

    val filtered = if (search.isBlank()) state.list
    else state.list.filter {
        it.name.contains(search, ignoreCase = true) ||
        it.city.orEmpty().contains(search, ignoreCase = true) ||
        it.address.orEmpty().contains(search, ignoreCase = true)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Рейтинг СТО", fontWeight = FontWeight.Bold) }) }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Поиск
            OutlinedTextField(
                value         = search,
                onValueChange = { search = it },
                placeholder   = { Text("Поиск по названию, городу...") },
                leadingIcon   = { Icon(Icons.Default.Search, null) },
                trailingIcon  = {
                    if (search.isNotEmpty()) {
                        IconButton(onClick = { search = "" }) { Icon(Icons.Default.Clear, "Очистить") }
                    }
                },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )

            when {
                state.isLoading      -> Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
                filtered.isEmpty()   -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (search.isBlank()) "Нет данных" else "Ничего не найдено по «$search»", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                else -> StoList(filtered)
            }
        }
    }
}

@Composable
private fun StoList(list: List<Sto>) {
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(list, key = { it.id }) { sto -> StoCard(sto) }
    }
}

@Composable
private fun StoCard(sto: Sto) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(if (sto.is_premium) 4.dp else 2.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Build, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(sto.name, fontWeight = FontWeight.Bold)
                        if (sto.is_premium) {
                            Text("⭐ Premium", color = Color(0xFFFFA000), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                // Рейтинг
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, Modifier.size(18.dp), tint = Color(0xFFFFA000))
                    Spacer(Modifier.width(2.dp))
                    Text("%.1f".format(sto.rating), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            if (!sto.city.isNullOrBlank()) {
                Text("📍 ${sto.city}${if (!sto.address.isNullOrBlank()) ", ${sto.address}" else ""}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else if (!sto.address.isNullOrBlank()) {
                Text("📍 ${sto.address}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (!sto.phone.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("📞 ${sto.phone}", style = MaterialTheme.typography.bodySmall)
            }

            if (sto.reviews_count > 0) {
                Spacer(Modifier.height(4.dp))
                Text("${sto.reviews_count} отзывов", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
