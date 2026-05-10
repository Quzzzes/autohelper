package by.autohelper.features.garage.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import by.autohelper.core.network.Car

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarageScreen(viewModel: GarageViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мой гараж", fontWeight = FontWeight.Bold) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить авто")
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.cars.isEmpty() -> EmptyGarageState(onAdd = { showAddDialog = true })
                else -> CarsList(cars = state.cars)
            }
        }
    }

    if (showAddDialog) {
        AddCarDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { plate, make, model, year ->
                viewModel.addCar(plate, make, model, year)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun CarsList(cars: List<Car>) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(cars) { car -> CarCard(car) }
    }
}

@Composable
private fun CarCard(car: Car) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DirectionsCar, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text("${car.make} ${car.model}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(car.plate, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${car.year} год", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun EmptyGarageState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(Icons.Default.DirectionsCar, null, Modifier.size(80.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
        Spacer(Modifier.height(16.dp))
        Text("Гараж пуст", style = MaterialTheme.typography.titleLarge)
        Text("Добавьте свой автомобиль", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onAdd) { Text("Добавить авто") }
    }
}

@Composable
private fun AddCarDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, Int) -> Unit) {
    var plate by remember { mutableStateOf("") }
    var make  by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year  by remember { mutableStateOf("2020") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить автомобиль") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(plate, { plate = it }, label = { Text("Госномер (АВ1234-7)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(make,  { make  = it }, label = { Text("Марка") },  modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(model, { model = it }, label = { Text("Модель") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(year,  { year  = it }, label = { Text("Год") },    modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(plate, make, model, year.toIntOrNull() ?: 2020) }) { Text("Добавить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
