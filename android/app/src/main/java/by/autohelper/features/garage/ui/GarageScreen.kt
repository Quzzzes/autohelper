package by.autohelper.features.garage.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
    var carToDelete by remember { mutableStateOf<Car?>(null) }

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
                else -> CarsList(
                    cars = state.cars,
                    selectedCar = state.selectedCar,
                    onSelect = { viewModel.selectCar(it) },
                    onDelete = { carToDelete = it },
                )
            }
            state.error?.let {
                Snackbar(Modifier.align(Alignment.BottomCenter).padding(16.dp)) { Text(it) }
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

    carToDelete?.let { car ->
        AlertDialog(
            onDismissRequest = { carToDelete = null },
            title = { Text("Удалить автомобиль?") },
            text  = { Text("${car.make} ${car.model} (${car.plate}) будет удалён из гаража.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteCar(car.id); carToDelete = null },
                    colors  = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { carToDelete = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun CarsList(
    cars: List<Car>,
    selectedCar: Car?,
    onSelect: (Car) -> Unit,
    onDelete: (Car) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(cars) { car ->
            CarCard(
                car        = car,
                isSelected = car.id == selectedCar?.id,
                onSelect   = { onSelect(car) },
                onDelete   = { onDelete(car) },
            )
        }
    }
}

@Composable
private fun CarCard(car: Car, isSelected: Boolean, onSelect: () -> Unit, onDelete: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0f)
    val borderWidth = if (isSelected) 2.dp else 0.dp

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .border(borderWidth, borderColor, MaterialTheme.shapes.medium)
            .clickable { onSelect() },
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 2.dp),
        colors    = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.DirectionsCar, null,
                Modifier.size(48.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("${car.make} ${car.model}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(car.plate, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${car.year} год", style = MaterialTheme.typography.bodySmall)
                if (car.vin != null) Text("VIN: ${car.vin}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
                Text("Активное", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
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
                OutlinedTextField(make,  { make  = it }, label = { Text("Марка") },               modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(model, { model = it }, label = { Text("Модель") },              modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(year,  { year  = it }, label = { Text("Год") },                 modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(plate, make, model, year.toIntOrNull() ?: 2020) },
                enabled = plate.isNotBlank() && make.isNotBlank() && model.isNotBlank(),
            ) { Text("Добавить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )
}
